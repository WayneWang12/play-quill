## play-quill

play与quill较为简单的结合，具体使用参看`example`

## 如何使用

在项目中添加依赖：
```sbt
libraryDependencies += "io.github.waynewang12" %% "play-quill" % "0.0.1"
```

## 支持数据库
目前只支持Mysql，但是后续扩展比较简单，参照`QuillMysqlComponents`创建对应数据库的类型即可。