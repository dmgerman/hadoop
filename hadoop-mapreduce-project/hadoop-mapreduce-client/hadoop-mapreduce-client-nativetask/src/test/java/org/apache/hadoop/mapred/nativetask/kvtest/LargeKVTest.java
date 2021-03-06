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
name|junit
operator|.
name|AfterClass
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|LargeKVTest
specifier|public
class|class
name|LargeKVTest
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LargeKVTest
operator|.
name|class
argument_list|)
decl_stmt|;
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
DECL|field|nativeConf
specifier|private
specifier|static
name|Configuration
name|nativeConf
init|=
name|ScenarioConfiguration
operator|.
name|getNativeConfiguration
argument_list|()
decl_stmt|;
DECL|field|normalConf
specifier|private
specifier|static
name|Configuration
name|normalConf
init|=
name|ScenarioConfiguration
operator|.
name|getNormalConfiguration
argument_list|()
decl_stmt|;
static|static
block|{
name|nativeConf
operator|.
name|addResource
argument_list|(
name|TestConstants
operator|.
name|KVTEST_CONF_PATH
argument_list|)
expr_stmt|;
name|nativeConf
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
name|normalConf
operator|.
name|addResource
argument_list|(
name|TestConstants
operator|.
name|KVTEST_CONF_PATH
argument_list|)
expr_stmt|;
name|normalConf
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
block|}
annotation|@
name|Test
DECL|method|testKeySize ()
specifier|public
name|void
name|testKeySize
parameter_list|()
throws|throws
name|Exception
block|{
name|runKVSizeTests
argument_list|(
name|Text
operator|.
name|class
argument_list|,
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValueSize ()
specifier|public
name|void
name|testValueSize
parameter_list|()
throws|throws
name|Exception
block|{
name|runKVSizeTests
argument_list|(
name|IntWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|cleanUp ()
specifier|public
specifier|static
name|void
name|cleanUp
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|ScenarioConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVTEST_DIR
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
DECL|method|runKVSizeTests (Class<?> keyClass, Class<?> valueClass)
specifier|public
name|void
name|runKVSizeTests
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|keyClass
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|valueClass
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|keyClass
operator|.
name|equals
argument_list|(
name|Text
operator|.
name|class
argument_list|)
operator|&&
operator|!
name|valueClass
operator|.
name|equals
argument_list|(
name|Text
operator|.
name|class
argument_list|)
condition|)
block|{
return|return;
block|}
specifier|final
name|int
name|deafultKVSizeMaximum
init|=
literal|1
operator|<<
literal|22
decl_stmt|;
comment|// 4M
specifier|final
name|int
name|kvSizeMaximum
init|=
name|normalConf
operator|.
name|getInt
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVSIZE_MAX_LARGEKV_TEST
argument_list|,
name|deafultKVSizeMaximum
argument_list|)
decl_stmt|;
specifier|final
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|normalConf
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|65536
init|;
name|i
operator|<=
name|kvSizeMaximum
condition|;
name|i
operator|*=
literal|4
control|)
block|{
name|int
name|min
init|=
name|i
operator|/
literal|4
decl_stmt|;
name|int
name|max
init|=
name|i
decl_stmt|;
name|nativeConf
operator|.
name|set
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVSIZE_MIN
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|min
argument_list|)
argument_list|)
expr_stmt|;
name|nativeConf
operator|.
name|set
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVSIZE_MAX
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|max
argument_list|)
argument_list|)
expr_stmt|;
name|normalConf
operator|.
name|set
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVSIZE_MIN
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|min
argument_list|)
argument_list|)
expr_stmt|;
name|normalConf
operator|.
name|set
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVSIZE_MAX
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|max
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"===KV Size Test: min size: "
operator|+
name|min
operator|+
literal|", max size: "
operator|+
name|max
operator|+
literal|", keyClass: "
operator|+
name|keyClass
operator|.
name|getName
argument_list|()
operator|+
literal|", valueClass: "
operator|+
name|valueClass
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|inputPath
init|=
name|TestConstants
operator|.
name|NATIVETASK_KVTEST_INPUTDIR
operator|+
literal|"/LargeKV/"
operator|+
name|keyClass
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
operator|+
name|valueClass
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|nativeOutputPath
init|=
name|TestConstants
operator|.
name|NATIVETASK_KVTEST_NATIVE_OUTPUTDIR
operator|+
literal|"/LargeKV/"
operator|+
name|keyClass
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
operator|+
name|valueClass
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// if output file exists ,then delete it
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|nativeOutputPath
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|KVJob
name|nativeJob
init|=
operator|new
name|KVJob
argument_list|(
literal|"Test Large Value Size:"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
name|nativeConf
argument_list|,
name|keyClass
argument_list|,
name|valueClass
argument_list|,
name|inputPath
argument_list|,
name|nativeOutputPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"job should complete successfully"
argument_list|,
name|nativeJob
operator|.
name|runJob
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|normalOutputPath
init|=
name|TestConstants
operator|.
name|NATIVETASK_KVTEST_NORMAL_OUTPUTDIR
operator|+
literal|"/LargeKV/"
operator|+
name|keyClass
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
operator|+
name|valueClass
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// if output file exists ,then delete it
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|normalOutputPath
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|KVJob
name|normalJob
init|=
operator|new
name|KVJob
argument_list|(
literal|"Test Large Key Size:"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
name|normalConf
argument_list|,
name|keyClass
argument_list|,
name|valueClass
argument_list|,
name|inputPath
argument_list|,
name|normalOutputPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"job should complete successfully"
argument_list|,
name|normalJob
operator|.
name|runJob
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|compareRet
init|=
name|ResultVerifier
operator|.
name|verify
argument_list|(
name|normalOutputPath
argument_list|,
name|nativeOutputPath
argument_list|)
decl_stmt|;
specifier|final
name|String
name|reason
init|=
literal|"keytype: "
operator|+
name|keyClass
operator|.
name|getName
argument_list|()
operator|+
literal|", valuetype: "
operator|+
name|valueClass
operator|.
name|getName
argument_list|()
operator|+
literal|", failed with "
operator|+
operator|(
name|keyClass
operator|.
name|equals
argument_list|(
name|Text
operator|.
name|class
argument_list|)
condition|?
literal|"key"
else|:
literal|"value"
operator|)
operator|+
literal|", min size: "
operator|+
name|min
operator|+
literal|", max size: "
operator|+
name|max
operator|+
literal|", normal out: "
operator|+
name|normalOutputPath
operator|+
literal|", native Out: "
operator|+
name|nativeOutputPath
decl_stmt|;
name|assertEquals
argument_list|(
name|reason
argument_list|,
literal|true
argument_list|,
name|compareRet
argument_list|)
expr_stmt|;
name|ResultVerifier
operator|.
name|verifyCounters
argument_list|(
name|normalJob
operator|.
name|job
argument_list|,
name|nativeJob
operator|.
name|job
argument_list|)
expr_stmt|;
block|}
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

