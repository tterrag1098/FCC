.class public dummyprogramname
.super java/lang/Object

.field private static _runTimer LRunTimer;
.field private static _standardIn LPascalTextIn;

.field private static foo I

.method public <init>()V

	aload_0
	invokenonvirtual	java/lang/Object/<init>()V
	return

.limit locals 1
.limit stack 1
.end method

.method private static adder(I)I

.var 0 is num I
.var 1 is adder I


.line 9
	iconst_1
	istore_0

	iload_1
	ireturn

.limit locals 2
.limit stack 1
.end method

.method private static foobar(IF)I

.var 0 is test I
.var 1 is test2 F
.var 2 is foobar I


.line 14
