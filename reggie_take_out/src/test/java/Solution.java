import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Solution {
    @Test
    public void Testleet() {
        rob(new int[]{1,2,3,2});
    }

    public int rob(int[] nums){
        int n = nums.length;
        if(n==1){
            return nums[0];
        }
        if(n==2){
            return Math.max(nums[0],nums[1]);
        }
        return Math.max(
                nums[0]+rob1(Arrays.copyOfRange(nums,2,n-1)),
                rob1(Arrays.copyOfRange(nums,1,n))
        );
    }
    public int rob1(int[] nums) {
        int n = nums.length;
        if(n==0 || n==1){
            return 0;
        }
        if(n==1){
            return nums[0];
        }
        int[] f = new int[n];
        f[0] = nums[0];
        f[1] = Math.max(nums[0],nums[1]);
        for(int i=2;i<n;i++){
            f[i] = Math.max(f[i-2]+nums[i],f[i-1]);
        }
        return f[n-1];
    }
}
