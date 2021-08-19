## Play-quill

Simple combination for play and quill-jdbc-zio. Please refer to project `example` for concrete usage.

play与quill较为简单的结合，具体使用参看`example`

## 如何使用 How to use it

Add the dependency in `build.sbt`:

在项目中添加依赖：

```sbt
libraryDependencies += "io.github.waynewang12" %% "play-quill" % "0.0.1"
```

## 支持数据库 Supported databases
Support only Mysql currently. But it is easy to extend to other databases. Just refer to `QuillMysqlComponents` to create different database support.

目前只支持Mysql，但是后续扩展比较简单，参照`QuillMysqlComponents`创建对应数据库的类型即可。
