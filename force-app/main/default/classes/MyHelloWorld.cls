public class MyHelloWorld {
   public static void applyDiscount(Book__c[] books) {
      for (Book__c b :books){
         b.Price__c *= 0.9;
      }
      // 無限ループ追加
      int k = 0;
      int unUsedVariables = 0;
      boolean b = true;
      while (b) {
          k++;
      }
   }
   void method1(){
     for (int i = 0; i < 10; i++){
       insert new Account(name = ‘Name ’ + i);
     }
   }
}