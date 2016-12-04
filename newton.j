.class public newton
.super java/lang/Object

.field private static _runTimer LRunTimer;
.field private static _standardIn LPascalTextIn;

.field private static number I
.field private static root F
.field private static sqroot F

.method public <init>()V

	aload_0
	invokenonvirtual	java/lang/Object/<init>()V
	return

.limit locals 1
.limit stack 1
.end method

.method public static main([Ljava/lang/String;)V

	new	RunTimer
	dup
	invokenonvirtual	RunTimer/<init>()V
	putstatic	newton/_runTimer LRunTimer;
	new	PascalTextIn
	dup
	invokenonvirtual	PascalTextIn/<init>()V
	putstatic	newton/_standardIn LPascalTextIn;


.line 11
L001:
.line 12
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	invokevirtual	java/io/PrintStream.println()V
.line 13
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"Enter new number (0 to quit): "
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 14
	getstatic	newton/_standardIn LPascalTextIn;
	invokevirtual	PascalTextIn.readInteger()I
	putstatic	newton/number I
.line 16
	getstatic	newton/number I
	iconst_0
	if_icmpeq	L004
	iconst_0
	goto	L005
L004:
	iconst_1
L005:
	ifeq	L006
.line 17
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%12d%12.6f\n"
	iconst_2
	anewarray	java/lang/Object
	dup
	iconst_0
	getstatic	newton/number I
	invokestatic	java/lang/Integer.valueOf(I)Ljava/lang/Integer;
	aastore
	dup
	iconst_1
	fconst_0
	invokestatic	java/lang/Float.valueOf(F)Ljava/lang/Float;
	aastore
	invokestatic	java/lang/String/format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
	goto	L003
L006:
.line 19
	getstatic	newton/number I
	iconst_0
	if_icmplt	L008
	iconst_0
	goto	L009
L008:
	iconst_1
L009:
	ifeq	L010
.line 20
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"*** ERROR:  number < 0\n"
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
	goto	L007
L010:
.line 23
	getstatic	newton/number I
	i2d
	invokestatic	java/lang/Math/sqrt(D)D
	d2f
	putstatic	newton/sqroot F
.line 24
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%12d%12.6f\n"
	iconst_2
	anewarray	java/lang/Object
	dup
	iconst_0
	getstatic	newton/number I
	invokestatic	java/lang/Integer.valueOf(I)Ljava/lang/Integer;
	aastore
	dup
	iconst_1
	getstatic	newton/sqroot F
	invokestatic	java/lang/Float.valueOf(F)Ljava/lang/Float;
	aastore
	invokestatic	java/lang/String/format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 25
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	invokevirtual	java/io/PrintStream.println()V
.line 27
	fconst_1
	putstatic	newton/root F
.line 28
L011:
.line 29
	getstatic	newton/number I
	i2f
	getstatic	newton/root F
	fdiv
	getstatic	newton/root F
	fadd
	iconst_2
	i2f
	fdiv
	putstatic	newton/root F
.line 30
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%24.6f%12.2f%%\n"
	iconst_2
	anewarray	java/lang/Object
	dup
	iconst_0
	getstatic	newton/root F
	invokestatic	java/lang/Float.valueOf(F)Ljava/lang/Float;
	aastore
	dup
	iconst_1
	bipush	100
	i2f
	getstatic	newton/root F
	getstatic	newton/sqroot F
	fsub
	invokestatic	java/lang/Math/abs(F)F
	fmul
	getstatic	newton/sqroot F
	fdiv
	invokestatic	java/lang/Float.valueOf(F)Ljava/lang/Float;
	aastore
	invokestatic	java/lang/String/format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
	getstatic	newton/number I
	i2f
	getstatic	newton/root F
	dup
	fmul
	fdiv
	iconst_1
	i2f
	fsub
	invokestatic	java/lang/Math/abs(F)F
	ldc	1.0E-6
	fcmpg
	iflt	L013
	iconst_0
	goto	L014
L013:
	iconst_1
L014:
	ifne	L012
	goto	L011
L012:
L007:
L003:
	getstatic	newton/number I
	iconst_0
	if_icmpeq	L015
	iconst_0
	goto	L016
L015:
	iconst_1
L016:
	ifne	L002
	goto	L001
L002:

	getstatic	newton/_runTimer LRunTimer;
	invokevirtual	RunTimer.printElapsedTime()V

	return

.limit locals 1
.limit stack 9
.end method
