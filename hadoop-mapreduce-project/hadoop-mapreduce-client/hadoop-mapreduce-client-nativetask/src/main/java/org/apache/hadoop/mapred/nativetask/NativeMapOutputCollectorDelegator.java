begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
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
name|classification
operator|.
name|InterfaceAudience
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
name|io
operator|.
name|RawComparator
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
name|InvalidJobConfException
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
name|MapOutputCollector
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
name|TaskAttemptID
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
name|handlers
operator|.
name|NativeCollectorOnlyHandler
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
name|INativeSerializer
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
name|NativeSerialization
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
name|MRConfig
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
name|TaskCounter
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
name|util
operator|.
name|QuickSort
import|;
end_import

begin_comment
comment|/**  * native map output collector wrapped in Java interface  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|NativeMapOutputCollectorDelegator
specifier|public
class|class
name|NativeMapOutputCollectorDelegator
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
implements|implements
name|MapOutputCollector
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
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
name|NativeMapOutputCollectorDelegator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|job
specifier|private
name|JobConf
name|job
decl_stmt|;
DECL|field|handler
specifier|private
name|NativeCollectorOnlyHandler
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|handler
decl_stmt|;
DECL|field|context
specifier|private
name|Context
name|context
decl_stmt|;
DECL|field|updater
specifier|private
name|StatusReportChecker
name|updater
decl_stmt|;
annotation|@
name|Override
DECL|method|collect (K key, V value, int partition)
specifier|public
name|void
name|collect
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|,
name|int
name|partition
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|handler
operator|.
name|collect
argument_list|(
name|key
argument_list|,
name|value
argument_list|,
name|partition
argument_list|)
expr_stmt|;
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
throws|,
name|InterruptedException
block|{
name|handler
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|updater
condition|)
block|{
name|updater
operator|.
name|stop
argument_list|()
expr_stmt|;
name|NativeRuntime
operator|.
name|reportStatus
argument_list|(
name|context
operator|.
name|getReporter
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|ClassNotFoundException
block|{
name|handler
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|init (Context context)
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|job
operator|=
name|context
operator|.
name|getJobConf
argument_list|()
expr_stmt|;
name|Platforms
operator|.
name|init
argument_list|(
name|job
argument_list|)
expr_stmt|;
if|if
condition|(
name|job
operator|.
name|getNumReduceTasks
argument_list|()
operator|==
literal|0
condition|)
block|{
name|String
name|message
init|=
literal|"There is no reducer, no need to use native output collector"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|InvalidJobConfException
argument_list|(
name|message
argument_list|)
throw|;
block|}
name|Class
argument_list|<
name|?
argument_list|>
name|comparatorClass
init|=
name|job
operator|.
name|getClass
argument_list|(
name|MRJobConfig
operator|.
name|KEY_COMPARATOR
argument_list|,
literal|null
argument_list|,
name|RawComparator
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|comparatorClass
operator|!=
literal|null
operator|&&
operator|!
name|Platforms
operator|.
name|define
argument_list|(
name|comparatorClass
argument_list|)
condition|)
block|{
name|String
name|message
init|=
literal|"Native output collector doesn't support customized java comparator "
operator|+
name|job
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|KEY_COMPARATOR
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|InvalidJobConfException
argument_list|(
name|message
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|QuickSort
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|job
operator|.
name|get
argument_list|(
name|Constants
operator|.
name|MAP_SORT_CLASS
argument_list|)
argument_list|)
condition|)
block|{
name|String
name|message
init|=
literal|"Native-Task doesn't support sort class "
operator|+
name|job
operator|.
name|get
argument_list|(
name|Constants
operator|.
name|MAP_SORT_CLASS
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|InvalidJobConfException
argument_list|(
name|message
argument_list|)
throw|;
block|}
if|if
condition|(
name|job
operator|.
name|getBoolean
argument_list|(
name|MRConfig
operator|.
name|SHUFFLE_SSL_ENABLED_KEY
argument_list|,
literal|false
argument_list|)
operator|==
literal|true
condition|)
block|{
name|String
name|message
init|=
literal|"Native-Task doesn't support secure shuffle"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|InvalidJobConfException
argument_list|(
name|message
argument_list|)
throw|;
block|}
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|keyCls
init|=
name|job
operator|.
name|getMapOutputKeyClass
argument_list|()
decl_stmt|;
try|try
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|final
name|INativeSerializer
name|serializer
init|=
name|NativeSerialization
operator|.
name|getInstance
argument_list|()
operator|.
name|getSerializer
argument_list|(
name|keyCls
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|serializer
condition|)
block|{
name|String
name|message
init|=
literal|"Key type not supported. Cannot find serializer for "
operator|+
name|keyCls
operator|.
name|getName
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|InvalidJobConfException
argument_list|(
name|message
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|Platforms
operator|.
name|support
argument_list|(
name|keyCls
operator|.
name|getName
argument_list|()
argument_list|,
name|serializer
argument_list|,
name|job
argument_list|)
condition|)
block|{
name|String
name|message
init|=
literal|"Native output collector doesn't support this key, "
operator|+
literal|"this key is not comparable in native: "
operator|+
name|keyCls
operator|.
name|getName
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|InvalidJobConfException
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Cannot find serializer for "
operator|+
name|keyCls
operator|.
name|getName
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|message
argument_list|)
throw|;
block|}
specifier|final
name|boolean
name|ret
init|=
name|NativeRuntime
operator|.
name|isNativeLibraryLoaded
argument_list|()
decl_stmt|;
if|if
condition|(
name|ret
condition|)
block|{
if|if
condition|(
name|job
operator|.
name|getBoolean
argument_list|(
name|MRJobConfig
operator|.
name|MAP_OUTPUT_COMPRESS
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|String
name|codec
init|=
name|job
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|MAP_OUTPUT_COMPRESS_CODEC
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|NativeRuntime
operator|.
name|supportsCompressionCodec
argument_list|(
name|codec
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
condition|)
block|{
name|String
name|message
init|=
literal|"Native output collector doesn't support compression codec "
operator|+
name|codec
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|InvalidJobConfException
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
name|NativeRuntime
operator|.
name|configure
argument_list|(
name|job
argument_list|)
expr_stmt|;
specifier|final
name|long
name|updateInterval
init|=
name|job
operator|.
name|getLong
argument_list|(
name|Constants
operator|.
name|NATIVE_STATUS_UPDATE_INTERVAL
argument_list|,
name|Constants
operator|.
name|NATIVE_STATUS_UPDATE_INTERVAL_DEFVAL
argument_list|)
decl_stmt|;
name|updater
operator|=
operator|new
name|StatusReportChecker
argument_list|(
name|context
operator|.
name|getReporter
argument_list|()
argument_list|,
name|updateInterval
argument_list|)
expr_stmt|;
name|updater
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|String
name|message
init|=
literal|"NativeRuntime cannot be loaded, please check that "
operator|+
literal|"libnativetask.so is in hadoop library dir"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|InvalidJobConfException
argument_list|(
name|message
argument_list|)
throw|;
block|}
name|this
operator|.
name|handler
operator|=
literal|null
expr_stmt|;
try|try
block|{
specifier|final
name|Class
argument_list|<
name|K
argument_list|>
name|oKClass
init|=
operator|(
name|Class
argument_list|<
name|K
argument_list|>
operator|)
name|job
operator|.
name|getMapOutputKeyClass
argument_list|()
decl_stmt|;
specifier|final
name|Class
argument_list|<
name|K
argument_list|>
name|oVClass
init|=
operator|(
name|Class
argument_list|<
name|K
argument_list|>
operator|)
name|job
operator|.
name|getMapOutputValueClass
argument_list|()
decl_stmt|;
specifier|final
name|TaskAttemptID
name|id
init|=
name|context
operator|.
name|getMapTask
argument_list|()
operator|.
name|getTaskID
argument_list|()
decl_stmt|;
specifier|final
name|TaskContext
name|taskContext
init|=
operator|new
name|TaskContext
argument_list|(
name|job
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|oKClass
argument_list|,
name|oVClass
argument_list|,
name|context
operator|.
name|getReporter
argument_list|()
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|handler
operator|=
name|NativeCollectorOnlyHandler
operator|.
name|create
argument_list|(
name|taskContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Native output collector cannot be loaded;"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|message
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Native output collector can be successfully enabled!"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

