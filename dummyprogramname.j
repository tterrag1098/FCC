.class public dummyprogramname
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

.line 4
	iconst_1
	ireturn

.limit locals 0
.limit stack 1
.end method

.method public static main([Ljava/lang/String;)V

	.var 2 is k I
	
	new	RunTimer
	dup
	invokenonvirtual	RunTimer/<init>()V
	putstatic	dummyprogramname/_runTimer LRunTimer;
	new	PascalTextIn
	dup
	invokenonvirtual	PascalTextIn/<init>()V
	putstatic	dummyprogramname/_standardIn LPascalTextIn;


.line 9
	invokestatic	dummyprogramname/foo()I
	istore_2
.line 11
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%d"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	iload_2
	invokestatic	java/lang/Integer.valueOf(I)Ljava/lang/Integer;
	aastore
	invokestatic	java/lang/String/format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V

	getstatic	dummyprogramname/_runTimer LRunTimer;
	invokevirtual	RunTimer.printElapsedTime()V

	return

.limit locals 3
.limit stack 7
.end method
