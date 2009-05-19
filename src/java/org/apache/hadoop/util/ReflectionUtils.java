begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

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
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|hadoop
operator|.
name|conf
operator|.
name|*
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
name|DataInputBuffer
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
name|DataOutputBuffer
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
name|Writable
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
name|serializer
operator|.
name|Deserializer
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
name|serializer
operator|.
name|SerializationFactory
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
name|serializer
operator|.
name|Serializer
import|;
end_import

begin_comment
comment|/**  * General reflection utils  */
end_comment

begin_class
DECL|class|ReflectionUtils
specifier|public
class|class
name|ReflectionUtils
block|{
DECL|field|EMPTY_ARRAY
specifier|private
specifier|static
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|Class
index|[]
block|{}
decl_stmt|;
DECL|field|serialFactory
specifier|private
specifier|static
name|SerializationFactory
name|serialFactory
init|=
literal|null
decl_stmt|;
comment|/**     * Cache of constructors for each class. Pins the classes so they    * can't be garbage collected until ReflectionUtils can be collected.    */
DECL|field|CONSTRUCTOR_CACHE
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Constructor
argument_list|<
name|?
argument_list|>
argument_list|>
name|CONSTRUCTOR_CACHE
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Constructor
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Check and set 'configuration' if necessary.    *     * @param theObject object for which to set configuration    * @param conf Configuration    */
DECL|method|setConf (Object theObject, Configuration conf)
specifier|public
specifier|static
name|void
name|setConf
parameter_list|(
name|Object
name|theObject
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|theObject
operator|instanceof
name|Configurable
condition|)
block|{
operator|(
operator|(
name|Configurable
operator|)
name|theObject
operator|)
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
name|setJobConf
argument_list|(
name|theObject
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This code is to support backward compatibility and break the compile      * time dependency of core on mapred.    * This should be made deprecated along with the mapred package HADOOP-1230.     * Should be removed when mapred package is removed.    */
DECL|method|setJobConf (Object theObject, Configuration conf)
specifier|private
specifier|static
name|void
name|setJobConf
parameter_list|(
name|Object
name|theObject
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
comment|//If JobConf and JobConfigurable are in classpath, AND
comment|//theObject is of type JobConfigurable AND
comment|//conf is of type JobConf then
comment|//invoke configure on theObject
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|jobConfClass
init|=
name|conf
operator|.
name|getClassByName
argument_list|(
literal|"org.apache.hadoop.mapred.JobConf"
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|jobConfigurableClass
init|=
name|conf
operator|.
name|getClassByName
argument_list|(
literal|"org.apache.hadoop.mapred.JobConfigurable"
argument_list|)
decl_stmt|;
if|if
condition|(
name|jobConfClass
operator|.
name|isAssignableFrom
argument_list|(
name|conf
operator|.
name|getClass
argument_list|()
argument_list|)
operator|&&
name|jobConfigurableClass
operator|.
name|isAssignableFrom
argument_list|(
name|theObject
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
name|Method
name|configureMethod
init|=
name|jobConfigurableClass
operator|.
name|getMethod
argument_list|(
literal|"configure"
argument_list|,
name|jobConfClass
argument_list|)
decl_stmt|;
name|configureMethod
operator|.
name|invoke
argument_list|(
name|theObject
argument_list|,
name|conf
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
comment|//JobConf/JobConfigurable not in classpath. no need to configure
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error in configuring object"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Create an object for the given class and initialize it from conf    *     * @param theClass class of which an object is created    * @param conf Configuration    * @return a new object    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|newInstance (Class<T> theClass, Configuration conf)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|newInstance
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|theClass
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|T
name|result
decl_stmt|;
try|try
block|{
name|Constructor
argument_list|<
name|T
argument_list|>
name|meth
init|=
operator|(
name|Constructor
argument_list|<
name|T
argument_list|>
operator|)
name|CONSTRUCTOR_CACHE
operator|.
name|get
argument_list|(
name|theClass
argument_list|)
decl_stmt|;
if|if
condition|(
name|meth
operator|==
literal|null
condition|)
block|{
name|meth
operator|=
name|theClass
operator|.
name|getDeclaredConstructor
argument_list|(
name|EMPTY_ARRAY
argument_list|)
expr_stmt|;
name|meth
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|CONSTRUCTOR_CACHE
operator|.
name|put
argument_list|(
name|theClass
argument_list|,
name|meth
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|meth
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|setConf
argument_list|(
name|result
argument_list|,
name|conf
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|field|threadBean
specifier|static
specifier|private
name|ThreadMXBean
name|threadBean
init|=
name|ManagementFactory
operator|.
name|getThreadMXBean
argument_list|()
decl_stmt|;
DECL|method|setContentionTracing (boolean val)
specifier|public
specifier|static
name|void
name|setContentionTracing
parameter_list|(
name|boolean
name|val
parameter_list|)
block|{
name|threadBean
operator|.
name|setThreadContentionMonitoringEnabled
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
DECL|method|getTaskName (long id, String name)
specifier|private
specifier|static
name|String
name|getTaskName
parameter_list|(
name|long
name|id
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|id
argument_list|)
return|;
block|}
return|return
name|id
operator|+
literal|" ("
operator|+
name|name
operator|+
literal|")"
return|;
block|}
comment|/**    * Print all of the thread's information and stack traces.    *     * @param stream the stream to    * @param title a string title for the stack trace    */
DECL|method|printThreadInfo (PrintWriter stream, String title)
specifier|public
specifier|static
name|void
name|printThreadInfo
parameter_list|(
name|PrintWriter
name|stream
parameter_list|,
name|String
name|title
parameter_list|)
block|{
specifier|final
name|int
name|STACK_DEPTH
init|=
literal|20
decl_stmt|;
name|boolean
name|contention
init|=
name|threadBean
operator|.
name|isThreadContentionMonitoringEnabled
argument_list|()
decl_stmt|;
name|long
index|[]
name|threadIds
init|=
name|threadBean
operator|.
name|getAllThreadIds
argument_list|()
decl_stmt|;
name|stream
operator|.
name|println
argument_list|(
literal|"Process Thread Dump: "
operator|+
name|title
argument_list|)
expr_stmt|;
name|stream
operator|.
name|println
argument_list|(
name|threadIds
operator|.
name|length
operator|+
literal|" active threads"
argument_list|)
expr_stmt|;
for|for
control|(
name|long
name|tid
range|:
name|threadIds
control|)
block|{
name|ThreadInfo
name|info
init|=
name|threadBean
operator|.
name|getThreadInfo
argument_list|(
name|tid
argument_list|,
name|STACK_DEPTH
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
name|stream
operator|.
name|println
argument_list|(
literal|"  Inactive"
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|stream
operator|.
name|println
argument_list|(
literal|"Thread "
operator|+
name|getTaskName
argument_list|(
name|info
operator|.
name|getThreadId
argument_list|()
argument_list|,
name|info
operator|.
name|getThreadName
argument_list|()
argument_list|)
operator|+
literal|":"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|State
name|state
init|=
name|info
operator|.
name|getThreadState
argument_list|()
decl_stmt|;
name|stream
operator|.
name|println
argument_list|(
literal|"  State: "
operator|+
name|state
argument_list|)
expr_stmt|;
name|stream
operator|.
name|println
argument_list|(
literal|"  Blocked count: "
operator|+
name|info
operator|.
name|getBlockedCount
argument_list|()
argument_list|)
expr_stmt|;
name|stream
operator|.
name|println
argument_list|(
literal|"  Waited count: "
operator|+
name|info
operator|.
name|getWaitedCount
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|contention
condition|)
block|{
name|stream
operator|.
name|println
argument_list|(
literal|"  Blocked time: "
operator|+
name|info
operator|.
name|getBlockedTime
argument_list|()
argument_list|)
expr_stmt|;
name|stream
operator|.
name|println
argument_list|(
literal|"  Waited time: "
operator|+
name|info
operator|.
name|getWaitedTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|state
operator|==
name|Thread
operator|.
name|State
operator|.
name|WAITING
condition|)
block|{
name|stream
operator|.
name|println
argument_list|(
literal|"  Waiting on "
operator|+
name|info
operator|.
name|getLockName
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|Thread
operator|.
name|State
operator|.
name|BLOCKED
condition|)
block|{
name|stream
operator|.
name|println
argument_list|(
literal|"  Blocked on "
operator|+
name|info
operator|.
name|getLockName
argument_list|()
argument_list|)
expr_stmt|;
name|stream
operator|.
name|println
argument_list|(
literal|"  Blocked by "
operator|+
name|getTaskName
argument_list|(
name|info
operator|.
name|getLockOwnerId
argument_list|()
argument_list|,
name|info
operator|.
name|getLockOwnerName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|stream
operator|.
name|println
argument_list|(
literal|"  Stack:"
argument_list|)
expr_stmt|;
for|for
control|(
name|StackTraceElement
name|frame
range|:
name|info
operator|.
name|getStackTrace
argument_list|()
control|)
block|{
name|stream
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|frame
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|stream
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
DECL|field|previousLogTime
specifier|private
specifier|static
name|long
name|previousLogTime
init|=
literal|0
decl_stmt|;
comment|/**    * Log the current thread stacks at INFO level.    * @param log the logger that logs the stack trace    * @param title a descriptive title for the call stacks    * @param minInterval the minimum time from the last     */
DECL|method|logThreadInfo (Log log, String title, long minInterval)
specifier|public
specifier|static
name|void
name|logThreadInfo
parameter_list|(
name|Log
name|log
parameter_list|,
name|String
name|title
parameter_list|,
name|long
name|minInterval
parameter_list|)
block|{
name|boolean
name|dumpStack
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|log
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
synchronized|synchronized
init|(
name|ReflectionUtils
operator|.
name|class
init|)
block|{
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|now
operator|-
name|previousLogTime
operator|>=
name|minInterval
operator|*
literal|1000
condition|)
block|{
name|previousLogTime
operator|=
name|now
expr_stmt|;
name|dumpStack
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|dumpStack
condition|)
block|{
name|ByteArrayOutputStream
name|buffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|printThreadInfo
argument_list|(
operator|new
name|PrintWriter
argument_list|(
name|buffer
argument_list|)
argument_list|,
name|title
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Return the correctly-typed {@link Class} of the given object.    *      * @param o object whose correctly-typed<code>Class</code> is to be obtained    * @return the correctly typed<code>Class</code> of the given object.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getClass (T o)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Class
argument_list|<
name|T
argument_list|>
name|getClass
parameter_list|(
name|T
name|o
parameter_list|)
block|{
return|return
operator|(
name|Class
argument_list|<
name|T
argument_list|>
operator|)
name|o
operator|.
name|getClass
argument_list|()
return|;
block|}
comment|// methods to support testing
DECL|method|clearCache ()
specifier|static
name|void
name|clearCache
parameter_list|()
block|{
name|CONSTRUCTOR_CACHE
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|getCacheSize ()
specifier|static
name|int
name|getCacheSize
parameter_list|()
block|{
return|return
name|CONSTRUCTOR_CACHE
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * A pair of input/output buffers that we use to clone writables.    */
DECL|class|CopyInCopyOutBuffer
specifier|private
specifier|static
class|class
name|CopyInCopyOutBuffer
block|{
DECL|field|outBuffer
name|DataOutputBuffer
name|outBuffer
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
DECL|field|inBuffer
name|DataInputBuffer
name|inBuffer
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
comment|/**      * Move the data from the output buffer to the input buffer.      */
DECL|method|moveData ()
name|void
name|moveData
parameter_list|()
block|{
name|inBuffer
operator|.
name|reset
argument_list|(
name|outBuffer
operator|.
name|getData
argument_list|()
argument_list|,
name|outBuffer
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Allocate a buffer for each thread that tries to clone objects.    */
DECL|field|cloneBuffers
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|CopyInCopyOutBuffer
argument_list|>
name|cloneBuffers
init|=
operator|new
name|ThreadLocal
argument_list|<
name|CopyInCopyOutBuffer
argument_list|>
argument_list|()
block|{
specifier|protected
specifier|synchronized
name|CopyInCopyOutBuffer
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|CopyInCopyOutBuffer
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|method|getFactory (Configuration conf)
specifier|private
specifier|static
name|SerializationFactory
name|getFactory
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|serialFactory
operator|==
literal|null
condition|)
block|{
name|serialFactory
operator|=
operator|new
name|SerializationFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
return|return
name|serialFactory
return|;
block|}
comment|/**    * Make a copy of the writable object using serialization to a buffer    * @param dst the object to copy from    * @param src the object to copy into, which is destroyed    * @throws IOException    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|copy (Configuration conf, T src, T dst)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|copy
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|T
name|src
parameter_list|,
name|T
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
name|CopyInCopyOutBuffer
name|buffer
init|=
name|cloneBuffers
operator|.
name|get
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|outBuffer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|SerializationFactory
name|factory
init|=
name|getFactory
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|T
argument_list|>
name|cls
init|=
operator|(
name|Class
argument_list|<
name|T
argument_list|>
operator|)
name|src
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|Serializer
argument_list|<
name|T
argument_list|>
name|serializer
init|=
name|factory
operator|.
name|getSerializer
argument_list|(
name|cls
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|open
argument_list|(
name|buffer
operator|.
name|outBuffer
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|serialize
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|moveData
argument_list|()
expr_stmt|;
name|Deserializer
argument_list|<
name|T
argument_list|>
name|deserializer
init|=
name|factory
operator|.
name|getDeserializer
argument_list|(
name|cls
argument_list|)
decl_stmt|;
name|deserializer
operator|.
name|open
argument_list|(
name|buffer
operator|.
name|inBuffer
argument_list|)
expr_stmt|;
name|dst
operator|=
name|deserializer
operator|.
name|deserialize
argument_list|(
name|dst
argument_list|)
expr_stmt|;
return|return
name|dst
return|;
block|}
annotation|@
name|Deprecated
DECL|method|cloneWritableInto (Writable dst, Writable src)
specifier|public
specifier|static
name|void
name|cloneWritableInto
parameter_list|(
name|Writable
name|dst
parameter_list|,
name|Writable
name|src
parameter_list|)
throws|throws
name|IOException
block|{
name|CopyInCopyOutBuffer
name|buffer
init|=
name|cloneBuffers
operator|.
name|get
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|outBuffer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|src
operator|.
name|write
argument_list|(
name|buffer
operator|.
name|outBuffer
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|moveData
argument_list|()
expr_stmt|;
name|dst
operator|.
name|readFields
argument_list|(
name|buffer
operator|.
name|inBuffer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

