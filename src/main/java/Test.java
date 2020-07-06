/**
 * @program: nio-rpc
 * @description:
 * @author: gzk
 * @create: 2020-06-01 15:38
 **/
public class Test {

    public static void main(String[] args) throws Exception{
        System.out.println( new Test().num(10));;
    }

    static int a = 1;
    // 100
    public int num(int x) throws Exception{

        if(a * a < x){
            a = a+1;
            num(x);
            return a-1;
        }else if(a * a == x){
            return a;
        }else{
            return a-1;
        }
    }

}
