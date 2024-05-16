package com.recognition.function;

import com.recognition.entity.*;
import com.recognition.enums.ExceptionEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class recognition {

    public static RecognitionResult recognize(List<Layer> openLayer, List<Layer> closeLayer, List<Goods> goodsList, List<Stock> stockList){
        RecognitionResult result=new RecognitionResult();
        List<RecognitionException> exceptions=new ArrayList<>();
        final List<RecognitionItem>[] items = new List[]{new ArrayList<>()};
        Map<String, Goods> goodsMap = new HashMap<>();
        goodsList.forEach(goods -> goodsMap.put(goods.getId(), goods));

        //获取各层架的重量差
        List<Layer> layers=new ArrayList<>();

        openLayer.forEach(open -> {
            //作为每一层架的重量差存储中介
            Layer layer1=new Layer();
            closeLayer.forEach(close->{
                //前后层架要一致
                if(open.getIndex()==close.getIndex()){
                    if(open.getWeight()<0||open.getWeight()>32767||close.getWeight()<0||close.getWeight()>32767){
                        //传感器异常
                        exceptions.add(new RecognitionException(open.getIndex(), ExceptionEnum.sensorException, open.getWeight(), close.getWeight()));
                    }else {
                        int diff=open.getWeight()-close.getWeight();
                        //前后重量不一致即说明有商品出售或异常
                        //有商品出售
                        if(diff>0){
                            layer1.setIndex(open.getIndex());
                            layer1.setWeight(diff);
                            layers.add(layer1);
                        } else if (diff < 0) {
                            //异物异常
                            exceptions.add(new RecognitionException(open.getIndex(), ExceptionEnum.differentException, open.getWeight(), close.getWeight()));
                        }
                    }
                }
            });
        });

        layers.forEach(layer -> {
            List<Stock> layerStocks=stockList.stream()
                    .filter(stock -> stock.getLayer()==layer.getIndex())
                    .collect(Collectors.toList());
            // 尝试找到匹配的商品
            RecognitionItem matchedItem = match(layer.getWeight(), layerStocks, goodsMap);
            if (matchedItem != null) {
                items[0] =addItem(items[0],matchedItem);
            }else {
                exceptions.add(new RecognitionException(layer.getIndex(), ExceptionEnum.UnableRecognizeException,0,0));
            }
        });
        result.setExceptions(exceptions);
        if(!exceptions.isEmpty()){
            result.setSuccessful(false);
            result.setItems(new ArrayList<>());
            return result;
        }else {
            result.setSuccessful(true);
            result.setItems(items[0]);
        }
        return result;
    }

    public static RecognitionItem match(int diff, List<Stock> layerStocks, Map<String, Goods> goodsMap){
        for(Stock stock:layerStocks){
            Goods goods = goodsMap.get(stock.getGoodsId());
            if (goods != null && diff % goods.getWeight() == 0) {
                int num = diff / goods.getWeight();
                if (num <= stock.getNum()) {
                    RecognitionItem item = new RecognitionItem();
                    item.setGoodsId(goods.getId());
                    item.setNum(num);
                    return item;
                }
            }
        }

        return null;
    }

    public static List<RecognitionItem> addItem(List<RecognitionItem> recognitionItems, RecognitionItem matchedItem){
        /*AtomicBoolean flag= new AtomicBoolean(true);
        recognitionItems.forEach(recognitionItem -> {
            if(recognitionItem.getGoodsId()==matchedItem.getGoodsId()){
                recognitionItem.setNum(recognitionItem.getNum()+matchedItem.getNum());
                flag.set(false);
            }
        });
        if(flag.get()==true){
            recognitionItems.add(matchedItem);
        }*/
        for (int i=0;i<recognitionItems.size();i++){
            if(recognitionItems.get(i).getGoodsId()==matchedItem.getGoodsId()){
                recognitionItems.get(i).setNum(recognitionItems.get(i).getNum()+matchedItem.getNum());
                return recognitionItems;
            }
        }
        recognitionItems.add(matchedItem);
        return recognitionItems;

    }

    public static void main(String[] args){

        List<Layer> open=new ArrayList<>();
        open.add(new Layer(1,2000));
        open.add(new Layer(2,2000));
        open.add(new Layer(3,2000));
        open.add(new Layer(4,2000));
        open.add(new Layer(5,2000));
        open.add(new Layer(6,4000));
        open.add(new Layer(7,2000));
        open.add(new Layer(8,2000));
        open.add(new Layer(9,2000));
        open.add(new Layer(10,2000));

        List<Layer> close=new ArrayList<>();
        close.add(new Layer(1,2000));
        close.add(new Layer(2,1800));
        close.add(new Layer(3,1466));
        close.add(new Layer(4,2000));
        close.add(new Layer(5,2000));
        close.add(new Layer(6,4000));
        close.add(new Layer(7,2000));
        close.add(new Layer(8,2000));
        close.add(new Layer(9,2000));
        close.add(new Layer(1,2000));

        List<Goods> goodsList=new ArrayList<>();
        goodsList.add(new Goods("000001",60));
        goodsList.add(new Goods("000002",88));
        goodsList.add(new Goods("000003",68));
        goodsList.add(new Goods("000004",123));
        goodsList.add(new Goods("000005",321));
        goodsList.add(new Goods("000006",550));
        goodsList.add(new Goods("000007",126));

        List<Stock> stockList=new ArrayList<>();
        stockList.add(new Stock("000001",1,10));
        stockList.add(new Stock("000002",2,10));
        stockList.add(new Stock("000003",3,10));
        stockList.add(new Stock("000004",4,8));
        stockList.add(new Stock("000005",5,13));
        stockList.add(new Stock("000006",6,6));
        stockList.add(new Stock("000007",7,7));
        stockList.add(new Stock("000001",8,10));
        stockList.add(new Stock("000003",9,12));
        stockList.add(new Stock("000007",10,8));
        stockList.add(new Stock("000004",3,8));
        stockList.add(new Stock("000005",4,9));


        RecognitionResult recognitionResult=recognize(open,close,goodsList,stockList);
        System.out.println(recognitionResult);


    }
}
