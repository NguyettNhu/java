package lab2;

import java.util.Scanner;

public class bai21 {

public static void main(String[] args ){
Scanner sc= new Scanner(System.in);
System.out.println("nhap diem chuyen can :");
double cc = sc.nextDouble ();
System.out.println("nhap diem giua ki :");
double gk = sc.nextDouble ();
System.out.println("nhap diem cuoi ki :");  
double ck = sc.nextDouble ();
double tong = cc * 0.1 + gk * 0.3 + ck * 0.6;
System.out.println("diem trung binh mon hoc la: " + tong);
}
}
