import org.junit.jupiter.api.Test;

import java.util.*;

class Solution {
    @Test
    public void Testleet() {
//        int[] res = maxAltitude(new int[]{7,2,4},2);
        Integer i1 = new Integer(1);
        Integer i2 = new Integer(1);
        if(i1==i2){
            System.out.println(i1.equals(i2));
        }

        Integer i3 = new Integer(1);
    }

    public int[] maxAltitude(int[] heights, int limit) {
        if(limit==0)return new int[0];
        int n =heights.length;
        int[] ans = new int[n-limit+1];
        Deque<Integer> q = new ArrayDeque();//单调队列，存的是下标
        for(int i=0;i<n;i++){
            while(!q.isEmpty() && q.getLast()<=heights[i]){//若队尾元素小于当前元素，队尾元素出队
                q.removeLast();//维护q的单调性
                //-----------------------  方向->递增
                //   1 -> 3 -> 4 -> 5
                //-----------------------
                //Last              First
            }
            //经过上步可以保证 此处 队列是单调的
            q.addLast(heights[i]);
            if(q.size()>limit){//窗口按递减顺序会发生这种情况 [7,2,4] limit=2 q:  ->2->7->  此时插入了4   4->2->7，由于窗口大小为2<3，需要7出列，并调整4的位置
                q.removeFirst();//7出列
                //接下来需要保证新插入元素heights[i]的位置正确；
                while(heights[i]>q.getFirst()){
                    q.pollFirst();
                }
            }
            if(i>=limit-1){
                ans[i-limit+1] = q.getFirst();
            }
        }
        return ans;
    }
}

