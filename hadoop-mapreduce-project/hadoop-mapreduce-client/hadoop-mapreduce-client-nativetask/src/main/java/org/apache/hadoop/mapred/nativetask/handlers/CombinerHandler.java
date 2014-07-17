begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.handlers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|nativetask
operator|.
name|handlers
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|Task
operator|.
name|Counter
operator|.
name|COMBINE_INPUT_RECORDS
import|;
end_import

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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|mapred
operator|.
name|Counters
operator|.
name|Counter
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
name|mapred
operator|.
name|JobConf
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
name|mapred
operator|.
name|Task
operator|.
name|CombinerRunner
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
name|mapred
operator|.
name|nativetask
operator|.
name|Command
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
name|mapred
operator|.
name|nativetask
operator|.
name|CommandDispatcher
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
name|mapred
operator|.
name|nativetask
operator|.
name|Constants
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
name|mapred
operator|.
name|nativetask
operator|.
name|DataChannel
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
name|mapred
operator|.
name|nativetask
operator|.
name|ICombineHandler
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
name|mapred
operator|.
name|nativetask
operator|.
name|INativeHandler
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
name|mapred
operator|.
name|nativetask
operator|.
name|NativeBatchProcessor
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
name|mapred
operator|.
name|nativetask
operator|.
name|TaskContext
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
name|mapred
operator|.
name|nativetask
operator|.
name|serde
operator|.
name|SerializationFramework
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
name|mapred
operator|.
name|nativetask
operator|.
name|util
operator|.
name|ReadWriteBuffer
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
name|mapreduce
operator|.
name|MRJobConfig
import|;
end_import

begin_class
DECL|class|CombinerHandler
specifier|public
class|class
name|CombinerHandler
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
implements|implements
name|ICombineHandler
implements|,
name|CommandDispatcher
block|{
DECL|field|NAME
specifier|public
specifier|static
name|String
name|NAME
init|=
literal|"NativeTask.CombineHandler"
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|NativeCollectorOnlyHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|LOAD
specifier|public
specifier|static
name|Command
name|LOAD
init|=
operator|new
name|Command
argument_list|(
literal|1
argument_list|,
literal|"Load"
argument_list|)
decl_stmt|;
DECL|field|COMBINE
specifier|public
specifier|static
name|Command
name|COMBINE
init|=
operator|new
name|Command
argument_list|(
literal|4
argument_list|,
literal|"Combine"
argument_list|)
decl_stmt|;
DECL|field|combinerRunner
specifier|public
specifier|final
name|CombinerRunner
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|combinerRunner
decl_stmt|;
DECL|field|nativeHandler
specifier|private
specifier|final
name|INativeHandler
name|nativeHandler
decl_stmt|;
DECL|field|puller
specifier|private
specifier|final
name|BufferPuller
name|puller
decl_stmt|;
DECL|field|kvPusher
specifier|private
specifier|final
name|BufferPusher
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|kvPusher
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|method|create (TaskContext context)
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|ICombineHandler
name|create
parameter_list|(
name|TaskContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
specifier|final
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|(
name|context
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|Constants
operator|.
name|SERIALIZATION_FRAMEWORK
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|SerializationFramework
operator|.
name|WRITABLE_SERIALIZATION
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|combinerClazz
init|=
name|conf
operator|.
name|get
argument_list|(
name|Constants
operator|.
name|MAPRED_COMBINER_CLASS
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|combinerClazz
condition|)
block|{
name|combinerClazz
operator|=
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|COMBINE_CLASS_ATTR
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|==
name|combinerClazz
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"NativeTask Combiner is enabled, class = "
operator|+
name|combinerClazz
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Counter
name|combineInputCounter
init|=
name|context
operator|.
name|getTaskReporter
argument_list|()
operator|.
name|getCounter
argument_list|(
name|COMBINE_INPUT_RECORDS
argument_list|)
decl_stmt|;
specifier|final
name|CombinerRunner
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|combinerRunner
init|=
name|CombinerRunner
operator|.
name|create
argument_list|(
name|conf
argument_list|,
name|context
operator|.
name|getTaskAttemptId
argument_list|()
argument_list|,
name|combineInputCounter
argument_list|,
name|context
operator|.
name|getTaskReporter
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|INativeHandler
name|nativeHandler
init|=
name|NativeBatchProcessor
operator|.
name|create
argument_list|(
name|NAME
argument_list|,
name|conf
argument_list|,
name|DataChannel
operator|.
name|INOUT
argument_list|)
decl_stmt|;
specifier|final
name|BufferPusher
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|pusher
init|=
operator|new
name|BufferPusher
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|(
name|context
operator|.
name|getInputKeyClass
argument_list|()
argument_list|,
name|context
operator|.
name|getInputValueClass
argument_list|()
argument_list|,
name|nativeHandler
argument_list|)
decl_stmt|;
specifier|final
name|BufferPuller
name|puller
init|=
operator|new
name|BufferPuller
argument_list|(
name|nativeHandler
argument_list|)
decl_stmt|;
return|return
operator|new
name|CombinerHandler
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|(
name|nativeHandler
argument_list|,
name|combinerRunner
argument_list|,
name|puller
argument_list|,
name|pusher
argument_list|)
return|;
block|}
DECL|method|CombinerHandler (INativeHandler nativeHandler, CombinerRunner<K, V> combiner, BufferPuller puller, BufferPusher<K, V> kvPusher)
specifier|public
name|CombinerHandler
parameter_list|(
name|INativeHandler
name|nativeHandler
parameter_list|,
name|CombinerRunner
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|combiner
parameter_list|,
name|BufferPuller
name|puller
parameter_list|,
name|BufferPusher
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|kvPusher
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|nativeHandler
operator|=
name|nativeHandler
expr_stmt|;
name|this
operator|.
name|combinerRunner
operator|=
name|combiner
expr_stmt|;
name|this
operator|.
name|puller
operator|=
name|puller
expr_stmt|;
name|this
operator|.
name|kvPusher
operator|=
name|kvPusher
expr_stmt|;
name|nativeHandler
operator|.
name|setCommandDispatcher
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|nativeHandler
operator|.
name|setDataReceiver
argument_list|(
name|puller
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onCall (Command command, ReadWriteBuffer parameter)
specifier|public
name|ReadWriteBuffer
name|onCall
parameter_list|(
name|Command
name|command
parameter_list|,
name|ReadWriteBuffer
name|parameter
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
literal|null
operator|==
name|command
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|command
operator|.
name|equals
argument_list|(
name|COMBINE
argument_list|)
condition|)
block|{
name|combine
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|combine ()
specifier|public
name|void
name|combine
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|puller
operator|.
name|reset
argument_list|()
expr_stmt|;
name|combinerRunner
operator|.
name|combine
argument_list|(
name|puller
argument_list|,
name|kvPusher
argument_list|)
expr_stmt|;
name|kvPusher
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getId ()
specifier|public
name|long
name|getId
parameter_list|()
block|{
return|return
name|nativeHandler
operator|.
name|getNativeHandler
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return;
block|}
if|if
condition|(
literal|null
operator|!=
name|puller
condition|)
block|{
name|puller
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|kvPusher
condition|)
block|{
name|kvPusher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|nativeHandler
condition|)
block|{
name|nativeHandler
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
end_class

end_unit

