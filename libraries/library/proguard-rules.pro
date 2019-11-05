-optimizationpasses 5
-dontusemixedcaseclassnames #表示混淆时不使用大小写混合类名。
-dontskipnonpubliclibraryclasses  #表示不跳过library中的非public的类。
-verbose  #表示打印混淆的详细信息。
-dontoptimize  #表示不进行优化，建议使用此选项，因为根据proguard-android-optimize.txt中的描述，优化可能会造成一些潜在风险，不能保证在所有版本的Dalvik上都正常运行。
-dontpreverify #表示不进行预校验。这个预校验是作用在Java平台上的，Android平台上不需要这项功能，去掉之后还可以加快混淆速度。
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/* #混淆时所采用的算法
-dontshrink    #不压缩输入的类文件


-ignorewarnings #忽略警告


-dontwarn com.baidu.**
-keep class com.baidu.**{*;}

-keep class com.example.library.**{*;}