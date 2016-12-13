.class public newton2
.super java/lang/Object

.field private static _runTimer LRunTimer;
.field private static _standardIn LPascalTextIn;

.field private static epsilon F
.field private static number I

.method public <init>()V

	aload_0
	invokenonvirtual	java/lang/Object/<init>()V
	return

.limit locals 1
.limit stack 1
.end method

.method private static root(F)F

.var 0 is x F
.var 1 is r F

.var 2 is root F



.line 14
	fconst_1
	fstore_1
.line 15
L001:
	fload_0
	fload_1
	dup
	fmul
	fdiv
	iconst_1
	i2f
	fsub
	invokestatic	java/lang/Math/abs(F)F
	getstatic	newton2/epsilon F
	fcmpg
	ifge	L003
	iconst_0
	goto	L004
L003:
	iconst_1
L004:
	iconst_1
	ixor
	ifne	L002
.line 17
	fload_0
	fload_1
	fdiv
	fload_1
	fadd
	iconst_2
	i2f
	fdiv
	fstore_1
	goto	L001
L002:
.line 20
	fload_1
	fstore_2

	fload_2
	freturn

.limit locals 3
.limit stack 3
.end method

.method private static print(IF)V

.var 0 is n I
.var 1 is root F




.line 26
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%d\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	iload_0
	invokestatic	java/lang/Integer.valueOf(I)Ljava/lang/Integer;
	aastore
	invokestatic	java/lang/String/format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 27
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%f\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	fload_1
	invokestatic	java/lang/Float.valueOf(F)Ljava/lang/Float;
	aastore
	invokestatic	java/lang/String/format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V

	return

.limit locals 2
.limit stack 7
.end method

.method public static main([Ljava/lang/String;)V

	new	RunTimer
	dup
	invokenonvirtual	RunTimer/<init>()V
	putstatic	newton2/_runTimer LRunTimer;
	new	PascalTextIn
	dup
	invokenonvirtual	PascalTextIn/<init>()V
	putstatic	newton2/_standardIn LPascalTextIn;



.var 0 is main Ljava/lang/StringBuilder;


.line 31
	ldc	6.0E-6
	putstatic	newton2/epsilon F
.line 32
	iconst_1
	ineg
	putstatic	newton2/number I
.line 33
L005:
	getstatic	newton2/number I
	iconst_0
	if_icmpne	L007
	iconst_0
	goto	L008
L007:
	iconst_1
L008:
	iconst_1
	ixor
	ifne	L006
.line 36
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n"
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 38
	getstatic	newton2/_standardIn LPascalTextIn;
	invokevirtual	PascalTextIn.readInteger()I
	putstatic	newton2/number I
.line 38
	getstatic	newton2/number I
	iconst_0
	if_icmpeq	L010
	iconst_0
	goto	L011
L010:
	iconst_1
L011:
	ifeq	L009
.line 41
	getstatic	newton2/number I
	fconst_0
	invokestatic	newton2/print(IF)V
L009:
.line 43
	getstatic	newton2/number I
	iconst_0
	if_icmpgt	L013
	iconst_0
	goto	L014
L013:
	iconst_1
L014:
	ifeq	L012
.line 46
	getstatic	newton2/number I
	getstatic	newton2/number I
	i2f
	invokestatic	newton2/root(F)F
	invokestatic	newton2/print(IF)V
L012:
	goto	L005
L006:

	getstatic	newton2/_runTimer LRunTimer;
	invokevirtual	RunTimer.printElapsedTime()V

	return

.limit locals 2
.limit stack 3
.end method
