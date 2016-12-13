.class public simple
.super java/lang/Object

.field private static _runTimer LRunTimer;
.field private static _standardIn LPascalTextIn;


.method public <init>()V

	aload_0
	invokenonvirtual	java/lang/Object/<init>()V
	return

.limit locals 1
.limit stack 1
.end method

.method private static foo()I


.var 0 is foo I



.line 4
	iconst_1
	istore_0

	iload_0
	ireturn

.limit locals 1
.limit stack 1
.end method

.method public static main([Ljava/lang/String;)V

	new	RunTimer
	dup
	invokenonvirtual	RunTimer/<init>()V
	putstatic	simple/_runTimer LRunTimer;
	new	PascalTextIn
	dup
	invokenonvirtual	PascalTextIn/<init>()V
	putstatic	simple/_standardIn LPascalTextIn;



.var 0 is main Ljava/lang/StringBuilder;
.var 2 is k I
.var 3 is j I


.line 9
	invokestatic	simple/foo()I
	istore_2
.line 10
	iload_2
	iconst_4
	iadd
	istore_3
.line 12
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%d\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	iload_2
	invokestatic	java/lang/Integer.valueOf(I)Ljava/lang/Integer;
	aastore
	invokestatic	java/lang/String/format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 13
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%d\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	iload_3
	invokestatic	java/lang/Integer.valueOf(I)Ljava/lang/Integer;
	aastore
	invokestatic	java/lang/String/format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V

	getstatic	simple/_runTimer LRunTimer;
	invokevirtual	RunTimer.printElapsedTime()V

	return

.limit locals 4
.limit stack 7
.end method
