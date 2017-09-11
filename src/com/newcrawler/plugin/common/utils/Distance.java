package com.newcrawler.plugin.common.utils;

/**
 * 计算文本相似度 
 * 
 * @author Administrator
 * 
 */
public class Distance {

	public static void main(String[] args) {
		String s1 = "罗峰是真被震撼了他不知道这躺着的类人形生 ";
		String s2 = "罗峰是真被震撼了他不知道这躺着的类人形生物";
		System.out.println(Distance.similarRate(s1, s2));
	}
	
	public static int similarRate(String s1, String s2){
		int n=LD(s1, s2);
		int l1=s1.length();
		int l2=s2.length();
		int l=(l1==l2?l1:(l1>l2?l2:l1));
		double r=1-(double)n/l;
		return (int)Math.round(r*100);
	}
	
	// *****************************
	// Compute Levenshtein distance
	// *****************************
	public static int LD(String s, String t) {
		int d[][]; // matrix
		int n; // length of s
		int m; // length of t
		int i; // iterates through s
		int j; // iterates through t
		char s_i; // ith character of s
		char t_j; // jth character of t
		int cost; // cost
		// Step 1
		n = s.length();
		m = t.length();
		if (n == 0) {
			return m;
		}
		if (m == 0) {
			return n;
		}
		d = new int[n + 1][m + 1];
		// Step 2
		for (i = 0; i <= n; i++) {
			d[i][0] = i;
		}
		for (j = 0; j <= m; j++) {
			d[0][j] = j;
		}
		// Step 3
		for (i = 1; i <= n; i++) {
			s_i = s.charAt(i - 1);
			// Step 4
			for (j = 1; j <= m; j++) {
				t_j = t.charAt(j - 1);
				// Step 5
				if (s_i == t_j) {
					cost = 0;
				} else {
					cost = 1;
				}
				// Step 6
				d[i][j] = Minimum(d[i - 1][j] + 1, d[i][j - 1] + 1,
						d[i - 1][j - 1] + cost);
			}
		}
		// Step 7
		return d[n][m];
	}
	
	// ****************************
	// Get minimum of three values
	// ****************************
	private static int Minimum(int a, int b, int c) {
		int mi = a;
		if (b < mi) {
			mi = b;
		}
		if (c < mi) {
			mi = c;
		}
		return mi;
	}

}