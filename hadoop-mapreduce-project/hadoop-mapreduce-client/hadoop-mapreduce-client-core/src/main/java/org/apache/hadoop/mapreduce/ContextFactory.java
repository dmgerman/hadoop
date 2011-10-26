begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * A factory to allow applications to deal with inconsistencies between  * MapReduce Context Objects API between hadoop-0.20 and later versions.  */
end_comment

begin_class
DECL|class|ContextFactory
specifier|public
class|class
name|ContextFactory
block|{
DECL|field|JOB_CONTEXT_CONSTRUCTOR
specifier|private
specifier|static
specifier|final
name|Constructor
argument_list|<
name|?
argument_list|>
name|JOB_CONTEXT_CONSTRUCTOR
decl_stmt|;
DECL|field|TASK_CONTEXT_CONSTRUCTOR
specifier|private
specifier|static
specifier|final
name|Constructor
argument_list|<
name|?
argument_list|>
name|TASK_CONTEXT_CONSTRUCTOR
decl_stmt|;
DECL|field|MAP_CONTEXT_CONSTRUCTOR
specifier|private
specifier|static
specifier|final
name|Constructor
argument_list|<
name|?
argument_list|>
name|MAP_CONTEXT_CONSTRUCTOR
decl_stmt|;
DECL|field|MAP_CONTEXT_IMPL_CONSTRUCTOR
specifier|private
specifier|static
specifier|final
name|Constructor
argument_list|<
name|?
argument_list|>
name|MAP_CONTEXT_IMPL_CONSTRUCTOR
decl_stmt|;
DECL|field|useV21
specifier|private
specifier|static
specifier|final
name|boolean
name|useV21
decl_stmt|;
DECL|field|REPORTER_FIELD
specifier|private
specifier|static
specifier|final
name|Field
name|REPORTER_FIELD
decl_stmt|;
DECL|field|READER_FIELD
specifier|private
specifier|static
specifier|final
name|Field
name|READER_FIELD
decl_stmt|;
DECL|field|WRITER_FIELD
specifier|private
specifier|static
specifier|final
name|Field
name|WRITER_FIELD
decl_stmt|;
DECL|field|OUTER_MAP_FIELD
specifier|private
specifier|static
specifier|final
name|Field
name|OUTER_MAP_FIELD
decl_stmt|;
DECL|field|WRAPPED_CONTEXT_FIELD
specifier|private
specifier|static
specifier|final
name|Field
name|WRAPPED_CONTEXT_FIELD
decl_stmt|;
static|static
block|{
name|boolean
name|v21
init|=
literal|true
decl_stmt|;
specifier|final
name|String
name|PACKAGE
init|=
literal|"org.apache.hadoop.mapreduce"
decl_stmt|;
try|try
block|{
name|Class
operator|.
name|forName
argument_list|(
name|PACKAGE
operator|+
literal|".task.JobContextImpl"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
name|v21
operator|=
literal|false
expr_stmt|;
block|}
name|useV21
operator|=
name|v21
expr_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|jobContextCls
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|taskContextCls
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|taskIOContextCls
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|mapCls
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|mapContextCls
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|innerMapContextCls
decl_stmt|;
try|try
block|{
if|if
condition|(
name|v21
condition|)
block|{
name|jobContextCls
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|PACKAGE
operator|+
literal|".task.JobContextImpl"
argument_list|)
expr_stmt|;
name|taskContextCls
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|PACKAGE
operator|+
literal|".task.TaskAttemptContextImpl"
argument_list|)
expr_stmt|;
name|taskIOContextCls
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|PACKAGE
operator|+
literal|".task.TaskInputOutputContextImpl"
argument_list|)
expr_stmt|;
name|mapContextCls
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|PACKAGE
operator|+
literal|".task.MapContextImpl"
argument_list|)
expr_stmt|;
name|mapCls
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|PACKAGE
operator|+
literal|".lib.map.WrappedMapper"
argument_list|)
expr_stmt|;
name|innerMapContextCls
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|PACKAGE
operator|+
literal|".lib.map.WrappedMapper$Context"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|jobContextCls
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|PACKAGE
operator|+
literal|".JobContext"
argument_list|)
expr_stmt|;
name|taskContextCls
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|PACKAGE
operator|+
literal|".TaskAttemptContext"
argument_list|)
expr_stmt|;
name|taskIOContextCls
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|PACKAGE
operator|+
literal|".TaskInputOutputContext"
argument_list|)
expr_stmt|;
name|mapContextCls
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|PACKAGE
operator|+
literal|".MapContext"
argument_list|)
expr_stmt|;
name|mapCls
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|PACKAGE
operator|+
literal|".Mapper"
argument_list|)
expr_stmt|;
name|innerMapContextCls
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|PACKAGE
operator|+
literal|".Mapper$Context"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't find class"
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|JOB_CONTEXT_CONSTRUCTOR
operator|=
name|jobContextCls
operator|.
name|getConstructor
argument_list|(
name|Configuration
operator|.
name|class
argument_list|,
name|JobID
operator|.
name|class
argument_list|)
expr_stmt|;
name|JOB_CONTEXT_CONSTRUCTOR
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TASK_CONTEXT_CONSTRUCTOR
operator|=
name|taskContextCls
operator|.
name|getConstructor
argument_list|(
name|Configuration
operator|.
name|class
argument_list|,
name|TaskAttemptID
operator|.
name|class
argument_list|)
expr_stmt|;
name|TASK_CONTEXT_CONSTRUCTOR
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|useV21
condition|)
block|{
name|MAP_CONTEXT_CONSTRUCTOR
operator|=
name|innerMapContextCls
operator|.
name|getConstructor
argument_list|(
name|mapCls
argument_list|,
name|MapContext
operator|.
name|class
argument_list|)
expr_stmt|;
name|MAP_CONTEXT_IMPL_CONSTRUCTOR
operator|=
name|mapContextCls
operator|.
name|getDeclaredConstructor
argument_list|(
name|Configuration
operator|.
name|class
argument_list|,
name|TaskAttemptID
operator|.
name|class
argument_list|,
name|RecordReader
operator|.
name|class
argument_list|,
name|RecordWriter
operator|.
name|class
argument_list|,
name|OutputCommitter
operator|.
name|class
argument_list|,
name|StatusReporter
operator|.
name|class
argument_list|,
name|InputSplit
operator|.
name|class
argument_list|)
expr_stmt|;
name|MAP_CONTEXT_IMPL_CONSTRUCTOR
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|WRAPPED_CONTEXT_FIELD
operator|=
name|innerMapContextCls
operator|.
name|getDeclaredField
argument_list|(
literal|"mapContext"
argument_list|)
expr_stmt|;
name|WRAPPED_CONTEXT_FIELD
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|MAP_CONTEXT_CONSTRUCTOR
operator|=
name|innerMapContextCls
operator|.
name|getConstructor
argument_list|(
name|mapCls
argument_list|,
name|Configuration
operator|.
name|class
argument_list|,
name|TaskAttemptID
operator|.
name|class
argument_list|,
name|RecordReader
operator|.
name|class
argument_list|,
name|RecordWriter
operator|.
name|class
argument_list|,
name|OutputCommitter
operator|.
name|class
argument_list|,
name|StatusReporter
operator|.
name|class
argument_list|,
name|InputSplit
operator|.
name|class
argument_list|)
expr_stmt|;
name|MAP_CONTEXT_IMPL_CONSTRUCTOR
operator|=
literal|null
expr_stmt|;
name|WRAPPED_CONTEXT_FIELD
operator|=
literal|null
expr_stmt|;
block|}
name|MAP_CONTEXT_CONSTRUCTOR
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|REPORTER_FIELD
operator|=
name|taskContextCls
operator|.
name|getDeclaredField
argument_list|(
literal|"reporter"
argument_list|)
expr_stmt|;
name|REPORTER_FIELD
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|READER_FIELD
operator|=
name|mapContextCls
operator|.
name|getDeclaredField
argument_list|(
literal|"reader"
argument_list|)
expr_stmt|;
name|READER_FIELD
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|WRITER_FIELD
operator|=
name|taskIOContextCls
operator|.
name|getDeclaredField
argument_list|(
literal|"output"
argument_list|)
expr_stmt|;
name|WRITER_FIELD
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|OUTER_MAP_FIELD
operator|=
name|innerMapContextCls
operator|.
name|getDeclaredField
argument_list|(
literal|"this$0"
argument_list|)
expr_stmt|;
name|OUTER_MAP_FIELD
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't run constructor "
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't find constructor "
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchFieldException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't find field "
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Clone a {@link JobContext} or {@link TaskAttemptContext} with a     * new configuration.    * @param original the original context    * @param conf the new configuration    * @return a new context object    * @throws InterruptedException     * @throws IOException     */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|cloneContext (JobContext original, Configuration conf )
specifier|public
specifier|static
name|JobContext
name|cloneContext
parameter_list|(
name|JobContext
name|original
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
try|try
block|{
if|if
condition|(
name|original
operator|instanceof
name|MapContext
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
condition|)
block|{
return|return
name|cloneMapContext
argument_list|(
operator|(
name|Mapper
operator|.
name|Context
operator|)
name|original
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|original
operator|instanceof
name|ReduceContext
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"can't clone ReduceContext"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|original
operator|instanceof
name|TaskAttemptContext
condition|)
block|{
name|TaskAttemptContext
name|spec
init|=
operator|(
name|TaskAttemptContext
operator|)
name|original
decl_stmt|;
return|return
operator|(
name|JobContext
operator|)
name|TASK_CONTEXT_CONSTRUCTOR
operator|.
name|newInstance
argument_list|(
name|conf
argument_list|,
name|spec
operator|.
name|getTaskAttemptID
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|(
name|JobContext
operator|)
name|JOB_CONTEXT_CONSTRUCTOR
operator|.
name|newInstance
argument_list|(
name|conf
argument_list|,
name|original
operator|.
name|getJobID
argument_list|()
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't clone object"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't clone object"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't clone object"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Copy a custom WrappedMapper.Context, optionally replacing     * the input and output.    * @param<K1> input key type    * @param<V1> input value type    * @param<K2> output key type    * @param<V2> output value type    * @param context the context to clone    * @param conf a new configuration    * @param reader Reader to read from. Null means to clone from context.    * @param writer Writer to write to. Null means to clone from context.    * @return a new context. it will not be the same class as the original.    * @throws IOException    * @throws InterruptedException    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
specifier|static
parameter_list|<
name|K1
parameter_list|,
name|V1
parameter_list|,
name|K2
parameter_list|,
name|V2
parameter_list|>
name|Mapper
argument_list|<
name|K1
argument_list|,
name|V1
argument_list|,
name|K2
argument_list|,
name|V2
argument_list|>
operator|.
name|Context
DECL|method|cloneMapContext (MapContext<K1,V1,K2,V2> context, Configuration conf, RecordReader<K1,V1> reader, RecordWriter<K2,V2> writer )
name|cloneMapContext
parameter_list|(
name|MapContext
argument_list|<
name|K1
argument_list|,
name|V1
argument_list|,
name|K2
argument_list|,
name|V2
argument_list|>
name|context
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|RecordReader
argument_list|<
name|K1
argument_list|,
name|V1
argument_list|>
name|reader
parameter_list|,
name|RecordWriter
argument_list|<
name|K2
argument_list|,
name|V2
argument_list|>
name|writer
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
try|try
block|{
comment|// get the outer object pointer
name|Object
name|outer
init|=
name|OUTER_MAP_FIELD
operator|.
name|get
argument_list|(
name|context
argument_list|)
decl_stmt|;
comment|// if it is a wrapped 21 context, unwrap it
if|if
condition|(
literal|"org.apache.hadoop.mapreduce.lib.map.WrappedMapper$Context"
operator|.
name|equals
argument_list|(
name|context
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|context
operator|=
operator|(
name|MapContext
argument_list|<
name|K1
argument_list|,
name|V1
argument_list|,
name|K2
argument_list|,
name|V2
argument_list|>
operator|)
name|WRAPPED_CONTEXT_FIELD
operator|.
name|get
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|// if the reader or writer aren't given, use the same ones
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
name|reader
operator|=
operator|(
name|RecordReader
argument_list|<
name|K1
argument_list|,
name|V1
argument_list|>
operator|)
name|READER_FIELD
operator|.
name|get
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
name|writer
operator|=
operator|(
name|RecordWriter
argument_list|<
name|K2
argument_list|,
name|V2
argument_list|>
operator|)
name|WRITER_FIELD
operator|.
name|get
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|useV21
condition|)
block|{
name|Object
name|basis
init|=
name|MAP_CONTEXT_IMPL_CONSTRUCTOR
operator|.
name|newInstance
argument_list|(
name|conf
argument_list|,
name|context
operator|.
name|getTaskAttemptID
argument_list|()
argument_list|,
name|reader
argument_list|,
name|writer
argument_list|,
name|context
operator|.
name|getOutputCommitter
argument_list|()
argument_list|,
name|REPORTER_FIELD
operator|.
name|get
argument_list|(
name|context
argument_list|)
argument_list|,
name|context
operator|.
name|getInputSplit
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|(
name|Mapper
operator|.
name|Context
operator|)
name|MAP_CONTEXT_CONSTRUCTOR
operator|.
name|newInstance
argument_list|(
name|outer
argument_list|,
name|basis
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|(
name|Mapper
operator|.
name|Context
operator|)
name|MAP_CONTEXT_CONSTRUCTOR
operator|.
name|newInstance
argument_list|(
name|outer
argument_list|,
name|conf
argument_list|,
name|context
operator|.
name|getTaskAttemptID
argument_list|()
argument_list|,
name|reader
argument_list|,
name|writer
argument_list|,
name|context
operator|.
name|getOutputCommitter
argument_list|()
argument_list|,
name|REPORTER_FIELD
operator|.
name|get
argument_list|(
name|context
argument_list|)
argument_list|,
name|context
operator|.
name|getInputSplit
argument_list|()
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't access field"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't create object"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't invoke constructor"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

