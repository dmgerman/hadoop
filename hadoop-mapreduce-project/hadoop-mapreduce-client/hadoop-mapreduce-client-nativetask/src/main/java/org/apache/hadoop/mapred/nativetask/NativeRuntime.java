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
name|conf
operator|.
name|Configuration
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
name|FloatWritable
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
name|IntWritable
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
name|LongWritable
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
name|Text
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
name|TaskReporter
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
name|ConfigUtil
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
name|VersionInfo
import|;
end_import

begin_comment
comment|/**  * This class stands for the native runtime It has three functions: 1. Create native handlers for map, reduce,  * outputcollector, and etc 2. Configure native task with provided MR configs 3. Provide file system api to native  * space, so that it can use File system like HDFS.  *   */
end_comment

begin_class
DECL|class|NativeRuntime
specifier|public
class|class
name|NativeRuntime
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
name|NativeRuntime
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|nativeLibraryLoaded
specifier|private
specifier|static
name|boolean
name|nativeLibraryLoaded
init|=
literal|false
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
static|static
block|{
try|try
block|{
name|System
operator|.
name|loadLibrary
argument_list|(
literal|"nativetask"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Nativetask JNI library loaded."
argument_list|)
expr_stmt|;
name|nativeLibraryLoaded
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Throwable
name|t
parameter_list|)
block|{
comment|// Ignore failures
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to load nativetask JNI library with error: "
operator|+
name|t
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"java.library.path="
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.library.path"
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"LD_LIBRARY_PATH="
operator|+
name|System
operator|.
name|getenv
argument_list|(
literal|"LD_LIBRARY_PATH"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertNativeLibraryLoaded ()
specifier|private
specifier|static
name|void
name|assertNativeLibraryLoaded
parameter_list|()
block|{
if|if
condition|(
operator|!
name|nativeLibraryLoaded
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Native runtime library not loaded"
argument_list|)
throw|;
block|}
block|}
DECL|method|isNativeLibraryLoaded ()
specifier|public
specifier|static
name|boolean
name|isNativeLibraryLoaded
parameter_list|()
block|{
return|return
name|nativeLibraryLoaded
return|;
block|}
DECL|method|configure (Configuration jobConf)
specifier|public
specifier|static
name|void
name|configure
parameter_list|(
name|Configuration
name|jobConf
parameter_list|)
block|{
name|assertNativeLibraryLoaded
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
name|jobConf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|Constants
operator|.
name|NATIVE_HADOOP_VERSION
argument_list|,
name|VersionInfo
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|JNIConfigure
argument_list|(
name|ConfigUtil
operator|.
name|toBytes
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * create native object We use it to create native handlers    *     * @param clazz    * @return    */
DECL|method|createNativeObject (String clazz)
specifier|public
specifier|synchronized
specifier|static
name|long
name|createNativeObject
parameter_list|(
name|String
name|clazz
parameter_list|)
block|{
name|assertNativeLibraryLoaded
argument_list|()
expr_stmt|;
specifier|final
name|long
name|ret
init|=
name|JNICreateNativeObject
argument_list|(
name|clazz
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Can't create NativeObject for class "
operator|+
name|clazz
operator|+
literal|", probably not exist."
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|/**    * Register a customized library    *     * @param clazz    * @return    */
DECL|method|registerLibrary (String libraryName, String clazz)
specifier|public
specifier|synchronized
specifier|static
name|long
name|registerLibrary
parameter_list|(
name|String
name|libraryName
parameter_list|,
name|String
name|clazz
parameter_list|)
block|{
name|assertNativeLibraryLoaded
argument_list|()
expr_stmt|;
specifier|final
name|long
name|ret
init|=
name|JNIRegisterModule
argument_list|(
name|libraryName
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
name|clazz
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|!=
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Can't create NativeObject for class "
operator|+
name|clazz
operator|+
literal|", probably not exist."
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|/**    * destroy native object We use to destory native handlers    */
DECL|method|releaseNativeObject (long addr)
specifier|public
specifier|synchronized
specifier|static
name|void
name|releaseNativeObject
parameter_list|(
name|long
name|addr
parameter_list|)
block|{
name|assertNativeLibraryLoaded
argument_list|()
expr_stmt|;
name|JNIReleaseNativeObject
argument_list|(
name|addr
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the status report from native space    *     * @param reporter    * @throws IOException    */
DECL|method|reportStatus (TaskReporter reporter)
specifier|public
specifier|static
name|void
name|reportStatus
parameter_list|(
name|TaskReporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
name|assertNativeLibraryLoaded
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|reporter
init|)
block|{
specifier|final
name|byte
index|[]
name|statusBytes
init|=
name|JNIUpdateStatus
argument_list|()
decl_stmt|;
specifier|final
name|DataInputBuffer
name|ib
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|ib
operator|.
name|reset
argument_list|(
name|statusBytes
argument_list|,
name|statusBytes
operator|.
name|length
argument_list|)
expr_stmt|;
specifier|final
name|FloatWritable
name|progress
init|=
operator|new
name|FloatWritable
argument_list|()
decl_stmt|;
name|progress
operator|.
name|readFields
argument_list|(
name|ib
argument_list|)
expr_stmt|;
name|reporter
operator|.
name|setProgress
argument_list|(
name|progress
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Text
name|status
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|status
operator|.
name|readFields
argument_list|(
name|ib
argument_list|)
expr_stmt|;
if|if
condition|(
name|status
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|reporter
operator|.
name|setStatus
argument_list|(
name|status
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|IntWritable
name|numCounters
init|=
operator|new
name|IntWritable
argument_list|()
decl_stmt|;
name|numCounters
operator|.
name|readFields
argument_list|(
name|ib
argument_list|)
expr_stmt|;
if|if
condition|(
name|numCounters
operator|.
name|get
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
specifier|final
name|Text
name|group
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
specifier|final
name|Text
name|name
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
specifier|final
name|LongWritable
name|amount
init|=
operator|new
name|LongWritable
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numCounters
operator|.
name|get
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|group
operator|.
name|readFields
argument_list|(
name|ib
argument_list|)
expr_stmt|;
name|name
operator|.
name|readFields
argument_list|(
name|ib
argument_list|)
expr_stmt|;
name|amount
operator|.
name|readFields
argument_list|(
name|ib
argument_list|)
expr_stmt|;
name|reporter
operator|.
name|incrCounter
argument_list|(
name|group
operator|.
name|toString
argument_list|()
argument_list|,
name|name
operator|.
name|toString
argument_list|()
argument_list|,
name|amount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/*******************************************************    *** The following are JNI Apis    ********************************************************/
comment|/**    * Check whether the native side has compression codec support built in    */
DECL|method|supportsCompressionCodec (byte[] codec)
specifier|public
specifier|native
specifier|static
name|boolean
name|supportsCompressionCodec
parameter_list|(
name|byte
index|[]
name|codec
parameter_list|)
function_decl|;
comment|/**    * Config the native runtime with mapreduce job configurations.    *     * @param configs    */
DECL|method|JNIConfigure (byte[][] configs)
specifier|private
specifier|native
specifier|static
name|void
name|JNIConfigure
parameter_list|(
name|byte
index|[]
index|[]
name|configs
parameter_list|)
function_decl|;
comment|/**    * create a native object in native space    *     * @param clazz    * @return    */
DECL|method|JNICreateNativeObject (byte[] clazz)
specifier|private
specifier|native
specifier|static
name|long
name|JNICreateNativeObject
parameter_list|(
name|byte
index|[]
name|clazz
parameter_list|)
function_decl|;
comment|/**    * create the default native object for certain type    *     * @param type    * @return    */
annotation|@
name|Deprecated
DECL|method|JNICreateDefaultNativeObject (byte[] type)
specifier|private
specifier|native
specifier|static
name|long
name|JNICreateDefaultNativeObject
parameter_list|(
name|byte
index|[]
name|type
parameter_list|)
function_decl|;
comment|/**    * destroy native object in native space    *     * @param addr    */
DECL|method|JNIReleaseNativeObject (long addr)
specifier|private
specifier|native
specifier|static
name|void
name|JNIReleaseNativeObject
parameter_list|(
name|long
name|addr
parameter_list|)
function_decl|;
comment|/**    * get status update from native side Encoding: progress:float status:Text Counter number: int the count of the    * counters Counters: array [group:Text, name:Text, incrCount:Long]    *     * @return    */
DECL|method|JNIUpdateStatus ()
specifier|private
specifier|native
specifier|static
name|byte
index|[]
name|JNIUpdateStatus
parameter_list|()
function_decl|;
comment|/**    * Not used.    */
DECL|method|JNIRelease ()
specifier|private
specifier|native
specifier|static
name|void
name|JNIRelease
parameter_list|()
function_decl|;
comment|/**    * Not used.    */
DECL|method|JNIRegisterModule (byte[] path, byte[] name)
specifier|private
specifier|native
specifier|static
name|int
name|JNIRegisterModule
parameter_list|(
name|byte
index|[]
name|path
parameter_list|,
name|byte
index|[]
name|name
parameter_list|)
function_decl|;
block|}
end_class

end_unit

