package sample;

public class MyQueue {
    public Object [] info;
    public int size, front, rear;
    public final int MAXSIZE = 8;
    public MyQueue(){
        info = new Object[MAXSIZE + 1];
        front = 0;
        rear = 0;
        size = 0;
    }
    public void add(Object a){
        info[rear]= a;
        if(rear == MAXSIZE) rear = 0;
        else rear ++;
        size++;
    }
    public Object remove(){
        Object r;
        r = info[front];
        info[front] = null;
        if(front == MAXSIZE)front = 0;
        else front++;
        size--;
        return r;
    }
    public Object show(int a){
        int results;
        if(a < size){
            results = a + front;
            if(results > MAXSIZE){
                results = results - MAXSIZE -1;
            }
            return info[results];
        }else{
            return null;
        }
    }
    public boolean isEmpty(){
        if(size < 1) return true;
        else return false;
    }
    public boolean isFull(){
        if(size >= MAXSIZE)return true;
        else return false;
    }
}

