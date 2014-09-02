begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.kvtest
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
name|kvtest
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|java
operator|.
name|util
operator|.
name|List
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|Path
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
name|mapred
operator|.
name|nativetask
operator|.
name|NativeRuntime
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
name|testutil
operator|.
name|ResultVerifier
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
name|testutil
operator|.
name|ScenarioConfiguration
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
name|testutil
operator|.
name|TestConstants
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
name|NativeCodeLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
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
name|Splitter
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
name|collect
operator|.
name|Lists
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|KVTest
specifier|public
class|class
name|KVTest
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|KVTest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|nativekvtestconf
specifier|private
specifier|static
name|Configuration
name|nativekvtestconf
init|=
name|ScenarioConfiguration
operator|.
name|getNativeConfiguration
argument_list|()
decl_stmt|;
DECL|field|hadoopkvtestconf
specifier|private
specifier|static
name|Configuration
name|hadoopkvtestconf
init|=
name|ScenarioConfiguration
operator|.
name|getNormalConfiguration
argument_list|()
decl_stmt|;
static|static
block|{
name|nativekvtestconf
operator|.
name|addResource
argument_list|(
name|TestConstants
operator|.
name|KVTEST_CONF_PATH
argument_list|)
expr_stmt|;
name|hadoopkvtestconf
operator|.
name|addResource
argument_list|(
name|TestConstants
operator|.
name|KVTEST_CONF_PATH
argument_list|)
expr_stmt|;
block|}
DECL|method|parseClassNames (String spec)
specifier|private
specifier|static
name|List
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|parseClassNames
parameter_list|(
name|String
name|spec
parameter_list|)
block|{
name|List
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|ret
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|classNames
init|=
name|Splitter
operator|.
name|on
argument_list|(
literal|';'
argument_list|)
operator|.
name|trimResults
argument_list|()
operator|.
name|omitEmptyStrings
argument_list|()
operator|.
name|split
argument_list|(
name|spec
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|className
range|:
name|classNames
control|)
block|{
try|try
block|{
name|ret
operator|.
name|add
argument_list|(
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
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
block|}
return|return
name|ret
return|;
block|}
comment|/**    * Parameterize the test with the specified key and value types.    */
annotation|@
name|Parameters
argument_list|(
name|name
operator|=
literal|"key:{0}\nvalue:{1}"
argument_list|)
DECL|method|data ()
specifier|public
specifier|static
name|Iterable
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
index|[]
argument_list|>
name|data
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Parse the config.
specifier|final
name|String
name|valueClassesStr
init|=
name|nativekvtestconf
operator|.
name|get
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVTEST_VALUECLASSES
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Parameterizing with value classes: "
operator|+
name|valueClassesStr
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|valueClasses
init|=
name|parseClassNames
argument_list|(
name|valueClassesStr
argument_list|)
decl_stmt|;
specifier|final
name|String
name|keyClassesStr
init|=
name|nativekvtestconf
operator|.
name|get
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVTEST_KEYCLASSES
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Parameterizing with key classes: "
operator|+
name|keyClassesStr
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|keyClasses
init|=
name|parseClassNames
argument_list|(
name|keyClassesStr
argument_list|)
decl_stmt|;
comment|// Generate an entry for each key type.
name|List
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
index|[]
argument_list|>
name|pairs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|keyClass
range|:
name|keyClasses
control|)
block|{
name|pairs
operator|.
name|add
argument_list|(
operator|new
name|Class
argument_list|<
name|?
argument_list|>
index|[]
block|{
name|keyClass
operator|,
name|LongWritable
operator|.
name|class
block|}
block|)
empty_stmt|;
block|}
comment|// ...and for each value type.
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|valueClass
range|:
name|valueClasses
control|)
block|{
name|pairs
operator|.
name|add
argument_list|(
operator|new
name|Class
argument_list|<
name|?
argument_list|>
index|[]
block|{
name|LongWritable
operator|.
name|class
operator|,
name|valueClass
block|}
block|)
empty_stmt|;
block|}
end_class

begin_return
return|return
name|pairs
return|;
end_return

begin_decl_stmt
unit|}    private
DECL|field|keyclass
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|keyclass
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|valueclass
specifier|private
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|valueclass
decl_stmt|;
end_decl_stmt

begin_constructor
DECL|method|KVTest (Class<?> keyclass, Class<?> valueclass)
specifier|public
name|KVTest
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|keyclass
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|valueclass
parameter_list|)
block|{
name|this
operator|.
name|keyclass
operator|=
name|keyclass
expr_stmt|;
name|this
operator|.
name|valueclass
operator|=
name|valueclass
expr_stmt|;
block|}
end_constructor

begin_function
annotation|@
name|Before
DECL|method|startUp ()
specifier|public
name|void
name|startUp
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|NativeCodeLoader
operator|.
name|isNativeCodeLoaded
argument_list|()
argument_list|)
expr_stmt|;
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|NativeRuntime
operator|.
name|isNativeLibraryLoaded
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
DECL|method|testKVCompability ()
specifier|public
name|void
name|testKVCompability
parameter_list|()
block|{
try|try
block|{
specifier|final
name|String
name|nativeoutput
init|=
name|this
operator|.
name|runNativeTest
argument_list|(
literal|"Test:"
operator|+
name|keyclass
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"--"
operator|+
name|valueclass
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|keyclass
argument_list|,
name|valueclass
argument_list|)
decl_stmt|;
specifier|final
name|String
name|normaloutput
init|=
name|this
operator|.
name|runNormalTest
argument_list|(
literal|"Test:"
operator|+
name|keyclass
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"--"
operator|+
name|valueclass
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|keyclass
argument_list|,
name|valueclass
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|compareRet
init|=
name|ResultVerifier
operator|.
name|verify
argument_list|(
name|normaloutput
argument_list|,
name|nativeoutput
argument_list|)
decl_stmt|;
specifier|final
name|String
name|input
init|=
name|nativekvtestconf
operator|.
name|get
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVTEST_INPUTDIR
argument_list|)
operator|+
literal|"/"
operator|+
name|keyclass
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
operator|+
name|valueclass
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|compareRet
condition|)
block|{
specifier|final
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|hadoopkvtestconf
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|nativeoutput
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|normaloutput
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|input
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"file compare result: if they are the same ,then return true"
argument_list|,
literal|true
argument_list|,
name|compareRet
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
name|assertEquals
argument_list|(
literal|"test run exception:"
argument_list|,
literal|null
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"test run exception:"
argument_list|,
literal|null
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
DECL|method|runNativeTest (String jobname, Class<?> keyclass, Class<?> valueclass)
specifier|private
name|String
name|runNativeTest
parameter_list|(
name|String
name|jobname
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|keyclass
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|valueclass
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|inputpath
init|=
name|nativekvtestconf
operator|.
name|get
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVTEST_INPUTDIR
argument_list|)
operator|+
literal|"/"
operator|+
name|keyclass
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
operator|+
name|valueclass
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|outputpath
init|=
name|nativekvtestconf
operator|.
name|get
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVTEST_OUTPUTDIR
argument_list|)
operator|+
literal|"/"
operator|+
name|keyclass
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
operator|+
name|valueclass
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// if output file exists ,then delete it
specifier|final
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|nativekvtestconf
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|outputpath
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|nativekvtestconf
operator|.
name|set
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVTEST_CREATEFILE
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|KVJob
name|keyJob
init|=
operator|new
name|KVJob
argument_list|(
name|jobname
argument_list|,
name|nativekvtestconf
argument_list|,
name|keyclass
argument_list|,
name|valueclass
argument_list|,
name|inputpath
argument_list|,
name|outputpath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"job should complete successfully"
argument_list|,
name|keyJob
operator|.
name|runJob
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|"native testcase run time error."
return|;
block|}
return|return
name|outputpath
return|;
block|}
end_function

begin_function
DECL|method|runNormalTest (String jobname, Class<?> keyclass, Class<?> valueclass)
specifier|private
name|String
name|runNormalTest
parameter_list|(
name|String
name|jobname
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|keyclass
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|valueclass
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|inputpath
init|=
name|hadoopkvtestconf
operator|.
name|get
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVTEST_INPUTDIR
argument_list|)
operator|+
literal|"/"
operator|+
name|keyclass
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
operator|+
name|valueclass
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|outputpath
init|=
name|hadoopkvtestconf
operator|.
name|get
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVTEST_NORMAL_OUTPUTDIR
argument_list|)
operator|+
literal|"/"
operator|+
name|keyclass
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
operator|+
name|valueclass
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// if output file exists ,then delete it
specifier|final
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|hadoopkvtestconf
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|outputpath
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|hadoopkvtestconf
operator|.
name|set
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVTEST_CREATEFILE
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|KVJob
name|keyJob
init|=
operator|new
name|KVJob
argument_list|(
name|jobname
argument_list|,
name|hadoopkvtestconf
argument_list|,
name|keyclass
argument_list|,
name|valueclass
argument_list|,
name|inputpath
argument_list|,
name|outputpath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"job should complete successfully"
argument_list|,
name|keyJob
operator|.
name|runJob
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|"normal testcase run time error."
return|;
block|}
return|return
name|outputpath
return|;
block|}
end_function

unit|}
end_unit

