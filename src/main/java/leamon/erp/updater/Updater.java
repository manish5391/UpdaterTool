package leamon.erp.updater;

import java.util.HashMap;
import java.util.Map;

public class Updater {

	public static  int findMax(int[] array) {
		if(array == null || array.length == 0){
			return -1;
		}      	

		int max = array[0];
		int loc = 0;
		for(int i = 1; i < array.length; i++ ){
			if(array[i] > max){
				max = array[i];
			}
		}

		for(int i=0; i<array.length; i++){
			if(max == array[i]){
				return i;
			}
		}
		return -1;
	}

	public static int findArray(int[] array, int[] subArray) {

		boolean result=true;
		int subArrayLength = subArray.length;

		if (subArrayLength == 0) {
			return -1;
		}
		int limit = array.length - subArrayLength;
		int i;
		for ( i = 0; i <= limit; i++)
			result = subArrayAppearsAt(array, subArray, i );

		if (result==true)
			return i;
		else
			return -1;

	}


	public static boolean subArrayAppearsAt(int[] largeArray, int[] subArray, int i) {

		if (subArray[0] == largeArray[i])
		{
			for (int j = 1; j < subArray.length; j++)
			{
				if (subArray[j] != largeArray[i+j])
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public static Map<String, String> decode(String s) {
		if(s==null){
			return null;
		}
		if(s.isEmpty()){
			return new HashMap<String, String>();
		}
		String vaString[] = s.split("&");
		Map<String, String> mp = new HashMap<String, String>();
		for(String va : vaString){
			String values [] = va.split("=");
			mp.put(values[0], values[1]);
		}
		return mp;
	}

	public static void main(String[] args) {
		//int arr [] = { 2,5,3,8,1,8};
		//int arr [] = { };
		final int[] arr = {1, 2, 3, 0, -1};
		int res = findMax(arr);
		System.out.println(res);

		String key = "one=1&two=2";
		System.out.println(decode(key));
	}
}
