# MiniTiktok
HashMap：Big Job

MVVM（Model-View-ViewModel）是一种高级项目架构模式

MVVM架构可以将程序结构主要分成3部分：Model是数据模型部分；View是界面展示部分；
而ViewModel比较特殊，可以将它理解成一个连接数据模型和界面展示的桥梁，从而实现让业务逻辑和界面展示分离的程序结构设计。

logic包用于存放业务逻辑相关的代码
logic包中又包含了dao、model、network这3个子包，分别用于存放数据访问对象、对象模型以及网络相关的代码

ui包用于存放界面展示相关的代码
ui包中包含主要界面子包，分别对应MinTiktok中的五个主要界面。

util包用于存放常用工具
