begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|security
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
name|assertNotNull
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|javax
operator|.
name|crypto
operator|.
name|KeyGenerator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|spec
operator|.
name|SecretKeySpec
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
name|codec
operator|.
name|binary
operator|.
name|Base64
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
name|hdfs
operator|.
name|HftpFileSystem
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
name|hdfs
operator|.
name|MiniDFSCluster
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
name|hdfs
operator|.
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenIdentifier
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
name|hdfs
operator|.
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenSecretManager
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|NameNode
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|NameNodeAdapter
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
name|MiniMRCluster
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
name|Job
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
name|SleepJob
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
name|server
operator|.
name|jobtracker
operator|.
name|JTConfig
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
name|security
operator|.
name|Credentials
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
name|security
operator|.
name|SecurityUtil
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|security
operator|.
name|token
operator|.
name|TokenIdentifier
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
name|ToolRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
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
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
import|;
end_import

begin_class
DECL|class|TestTokenCache
specifier|public
class|class
name|TestTokenCache
block|{
DECL|field|NUM_OF_KEYS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_OF_KEYS
init|=
literal|10
decl_stmt|;
comment|// my sleep class - adds check for tokenCache
DECL|class|MySleepMapper
specifier|static
class|class
name|MySleepMapper
extends|extends
name|SleepJob
operator|.
name|SleepMapper
block|{
comment|/**      * attempts to access tokenCache as from client      */
annotation|@
name|Override
DECL|method|map (IntWritable key, IntWritable value, Context context)
specifier|public
name|void
name|map
parameter_list|(
name|IntWritable
name|key
parameter_list|,
name|IntWritable
name|value
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|// get token storage and a key
name|Credentials
name|ts
init|=
name|context
operator|.
name|getCredentials
argument_list|()
decl_stmt|;
name|byte
index|[]
name|key1
init|=
name|ts
operator|.
name|getSecretKey
argument_list|(
operator|new
name|Text
argument_list|(
literal|"alias1"
argument_list|)
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
argument_list|>
name|dts
init|=
name|ts
operator|.
name|getAllTokens
argument_list|()
decl_stmt|;
name|int
name|dts_size
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|dts
operator|!=
literal|null
condition|)
name|dts_size
operator|=
name|dts
operator|.
name|size
argument_list|()
expr_stmt|;
if|if
condition|(
name|dts_size
operator|!=
literal|2
condition|)
block|{
comment|// one job token and one delegation token
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"tokens are not available"
argument_list|)
throw|;
comment|// fail the test
block|}
if|if
condition|(
name|key1
operator|==
literal|null
operator|||
name|ts
operator|==
literal|null
operator|||
name|ts
operator|.
name|numberOfSecretKeys
argument_list|()
operator|!=
name|NUM_OF_KEYS
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"secret keys are not available"
argument_list|)
throw|;
comment|// fail the test
block|}
name|super
operator|.
name|map
argument_list|(
name|key
argument_list|,
name|value
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MySleepJob
class|class
name|MySleepJob
extends|extends
name|SleepJob
block|{
annotation|@
name|Override
DECL|method|createJob (int numMapper, int numReducer, long mapSleepTime, int mapSleepCount, long reduceSleepTime, int reduceSleepCount)
specifier|public
name|Job
name|createJob
parameter_list|(
name|int
name|numMapper
parameter_list|,
name|int
name|numReducer
parameter_list|,
name|long
name|mapSleepTime
parameter_list|,
name|int
name|mapSleepCount
parameter_list|,
name|long
name|reduceSleepTime
parameter_list|,
name|int
name|reduceSleepCount
parameter_list|)
throws|throws
name|IOException
block|{
name|Job
name|job
init|=
name|super
operator|.
name|createJob
argument_list|(
name|numMapper
argument_list|,
name|numReducer
argument_list|,
name|mapSleepTime
argument_list|,
name|mapSleepCount
argument_list|,
name|reduceSleepTime
argument_list|,
name|reduceSleepCount
argument_list|)
decl_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|MySleepMapper
operator|.
name|class
argument_list|)
expr_stmt|;
comment|//Populate tokens here because security is disabled.
name|populateTokens
argument_list|(
name|job
argument_list|)
expr_stmt|;
return|return
name|job
return|;
block|}
DECL|method|populateTokens (Job job)
specifier|private
name|void
name|populateTokens
parameter_list|(
name|Job
name|job
parameter_list|)
block|{
comment|// Credentials in the job will not have delegation tokens
comment|// because security is disabled. Fetch delegation tokens
comment|// and populate the credential in the job.
try|try
block|{
name|Credentials
name|ts
init|=
name|job
operator|.
name|getCredentials
argument_list|()
decl_stmt|;
name|Path
name|p1
init|=
operator|new
name|Path
argument_list|(
literal|"file1"
argument_list|)
decl_stmt|;
name|p1
operator|=
name|p1
operator|.
name|getFileSystem
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|p1
argument_list|)
expr_stmt|;
name|Credentials
name|cred
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|TokenCache
operator|.
name|obtainTokensForNamenodesInternal
argument_list|(
name|cred
argument_list|,
operator|new
name|Path
index|[]
block|{
name|p1
block|}
argument_list|,
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|t
range|:
name|cred
operator|.
name|getAllTokens
argument_list|()
control|)
block|{
name|ts
operator|.
name|addToken
argument_list|(
operator|new
name|Text
argument_list|(
literal|"Hdfs"
argument_list|)
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|mrCluster
specifier|private
specifier|static
name|MiniMRCluster
name|mrCluster
decl_stmt|;
DECL|field|dfsCluster
specifier|private
specifier|static
name|MiniDFSCluster
name|dfsCluster
decl_stmt|;
DECL|field|TEST_DIR
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_DIR
init|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|,
literal|"sleepTest"
argument_list|)
decl_stmt|;
DECL|field|tokenFileName
specifier|private
specifier|static
specifier|final
name|Path
name|tokenFileName
init|=
operator|new
name|Path
argument_list|(
name|TEST_DIR
argument_list|,
literal|"tokenFile.json"
argument_list|)
decl_stmt|;
DECL|field|numSlaves
specifier|private
specifier|static
name|int
name|numSlaves
init|=
literal|1
decl_stmt|;
DECL|field|jConf
specifier|private
specifier|static
name|JobConf
name|jConf
decl_stmt|;
DECL|field|mapper
specifier|private
specifier|static
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
DECL|field|p1
specifier|private
specifier|static
name|Path
name|p1
decl_stmt|;
DECL|field|p2
specifier|private
specifier|static
name|Path
name|p2
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.security.auth_to_local"
argument_list|,
literal|"RULE:[2:$1]"
argument_list|)
expr_stmt|;
name|dfsCluster
operator|=
operator|new
name|MiniDFSCluster
argument_list|(
name|conf
argument_list|,
name|numSlaves
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|jConf
operator|=
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|mrCluster
operator|=
operator|new
name|MiniMRCluster
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|numSlaves
argument_list|,
name|dfsCluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|jConf
argument_list|)
expr_stmt|;
name|createTokenFileJson
argument_list|()
expr_stmt|;
name|verifySecretKeysInJSONFile
argument_list|()
expr_stmt|;
name|NameNodeAdapter
operator|.
name|getDtSecretManager
argument_list|(
name|dfsCluster
operator|.
name|getNamesystem
argument_list|()
argument_list|)
operator|.
name|startThreads
argument_list|()
expr_stmt|;
name|FileSystem
name|fs
init|=
name|dfsCluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|p1
operator|=
operator|new
name|Path
argument_list|(
literal|"file1"
argument_list|)
expr_stmt|;
name|p2
operator|=
operator|new
name|Path
argument_list|(
literal|"file2"
argument_list|)
expr_stmt|;
name|p1
operator|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|p1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|mrCluster
operator|!=
literal|null
condition|)
name|mrCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|mrCluster
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|dfsCluster
operator|!=
literal|null
condition|)
name|dfsCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dfsCluster
operator|=
literal|null
expr_stmt|;
block|}
comment|// create jason file and put some keys into it..
DECL|method|createTokenFileJson ()
specifier|private
specifier|static
name|void
name|createTokenFileJson
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|KeyGenerator
name|kg
init|=
name|KeyGenerator
operator|.
name|getInstance
argument_list|(
literal|"HmacSHA1"
argument_list|)
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
name|NUM_OF_KEYS
condition|;
name|i
operator|++
control|)
block|{
name|SecretKeySpec
name|key
init|=
operator|(
name|SecretKeySpec
operator|)
name|kg
operator|.
name|generateKey
argument_list|()
decl_stmt|;
name|byte
index|[]
name|enc_key
init|=
name|key
operator|.
name|getEncoded
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"alias"
operator|+
name|i
argument_list|,
operator|new
name|String
argument_list|(
name|Base64
operator|.
name|encodeBase64
argument_list|(
name|enc_key
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
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
try|try
block|{
name|File
name|p
init|=
operator|new
name|File
argument_list|(
name|tokenFileName
operator|.
name|getParent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|p
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
comment|// convert to JSON and save to the file
name|mapper
operator|.
name|writeValue
argument_list|(
operator|new
name|File
argument_list|(
name|tokenFileName
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"failed with :"
operator|+
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|verifySecretKeysInJSONFile ()
specifier|private
specifier|static
name|void
name|verifySecretKeysInJSONFile
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
decl_stmt|;
name|map
operator|=
name|mapper
operator|.
name|readValue
argument_list|(
operator|new
name|File
argument_list|(
name|tokenFileName
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|Map
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"didn't read JSON correctly"
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|,
name|NUM_OF_KEYS
argument_list|)
expr_stmt|;
block|}
comment|/**    * run a distributed job and verify that TokenCache is available    * @throws IOException    */
annotation|@
name|Test
DECL|method|testTokenCache ()
specifier|public
name|void
name|testTokenCache
parameter_list|()
throws|throws
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"running dist job"
argument_list|)
expr_stmt|;
comment|// make sure JT starts
name|jConf
operator|=
name|mrCluster
operator|.
name|createJobConf
argument_list|()
expr_stmt|;
comment|// provide namenodes names for the job to get the delegation tokens for
name|String
name|nnUri
init|=
name|dfsCluster
operator|.
name|getURI
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|jConf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|JOB_NAMENODES
argument_list|,
name|nnUri
operator|+
literal|","
operator|+
name|nnUri
argument_list|)
expr_stmt|;
comment|// job tracker principla id..
name|jConf
operator|.
name|set
argument_list|(
name|JTConfig
operator|.
name|JT_USER_NAME
argument_list|,
literal|"jt_id/foo@BAR"
argument_list|)
expr_stmt|;
comment|// using argument to pass the file name
name|String
index|[]
name|args
init|=
block|{
literal|"-tokenCacheFile"
block|,
name|tokenFileName
operator|.
name|toString
argument_list|()
block|,
literal|"-m"
block|,
literal|"1"
block|,
literal|"-r"
block|,
literal|"1"
block|,
literal|"-mt"
block|,
literal|"1"
block|,
literal|"-rt"
block|,
literal|"1"
block|}
decl_stmt|;
name|int
name|res
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|res
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|jConf
argument_list|,
operator|new
name|MySleepJob
argument_list|()
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Job failed with"
operator|+
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Job failed"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"dist job res is not 0"
argument_list|,
name|res
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * run a local job and verify that TokenCache is available    * @throws NoSuchAlgorithmException    * @throws IOException    */
annotation|@
name|Test
DECL|method|testLocalJobTokenCache ()
specifier|public
name|void
name|testLocalJobTokenCache
parameter_list|()
throws|throws
name|NoSuchAlgorithmException
throws|,
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"running local job"
argument_list|)
expr_stmt|;
comment|// this is local job
name|String
index|[]
name|args
init|=
block|{
literal|"-m"
block|,
literal|"1"
block|,
literal|"-r"
block|,
literal|"1"
block|,
literal|"-mt"
block|,
literal|"1"
block|,
literal|"-rt"
block|,
literal|"1"
block|}
decl_stmt|;
name|jConf
operator|.
name|set
argument_list|(
literal|"mapreduce.job.credentials.json"
argument_list|,
name|tokenFileName
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|res
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|res
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|jConf
argument_list|,
operator|new
name|MySleepJob
argument_list|()
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Job failed with"
operator|+
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"local Job failed"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"local job res is not 0"
argument_list|,
name|res
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetTokensForNamenodes ()
specifier|public
name|void
name|testGetTokensForNamenodes
parameter_list|()
throws|throws
name|IOException
block|{
name|Credentials
name|credentials
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|TokenCache
operator|.
name|obtainTokensForNamenodesInternal
argument_list|(
name|credentials
argument_list|,
operator|new
name|Path
index|[]
block|{
name|p1
block|,
name|p2
block|}
argument_list|,
name|jConf
argument_list|)
expr_stmt|;
comment|// this token is keyed by hostname:port key.
name|String
name|fs_addr
init|=
name|SecurityUtil
operator|.
name|buildDTServiceName
argument_list|(
name|p1
operator|.
name|toUri
argument_list|()
argument_list|,
name|NameNode
operator|.
name|DEFAULT_PORT
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|nnt
init|=
name|TokenCache
operator|.
name|getDelegationToken
argument_list|(
name|credentials
argument_list|,
name|fs_addr
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"dt for "
operator|+
name|p1
operator|+
literal|"("
operator|+
name|fs_addr
operator|+
literal|")"
operator|+
literal|" = "
operator|+
name|nnt
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Token for nn is null"
argument_list|,
name|nnt
argument_list|)
expr_stmt|;
comment|// verify the size
name|Collection
argument_list|<
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
argument_list|>
name|tns
init|=
name|credentials
operator|.
name|getAllTokens
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"number of tokens is not 1"
argument_list|,
literal|1
argument_list|,
name|tns
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|t
range|:
name|tns
control|)
block|{
if|if
condition|(
name|t
operator|.
name|getKind
argument_list|()
operator|.
name|equals
argument_list|(
name|DelegationTokenIdentifier
operator|.
name|HDFS_DELEGATION_KIND
argument_list|)
operator|&&
name|t
operator|.
name|getService
argument_list|()
operator|.
name|equals
argument_list|(
operator|new
name|Text
argument_list|(
name|fs_addr
argument_list|)
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"didn't find token for "
operator|+
name|p1
argument_list|,
name|found
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetTokensForHftpFS ()
specifier|public
name|void
name|testGetTokensForHftpFS
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|HftpFileSystem
name|hfs
init|=
name|mock
argument_list|(
name|HftpFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|DelegationTokenSecretManager
name|dtSecretManager
init|=
name|NameNodeAdapter
operator|.
name|getDtSecretManager
argument_list|(
name|dfsCluster
operator|.
name|getNamesystem
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|renewer
init|=
literal|"renewer"
decl_stmt|;
name|jConf
operator|.
name|set
argument_list|(
name|JTConfig
operator|.
name|JT_USER_NAME
argument_list|,
name|renewer
argument_list|)
expr_stmt|;
name|DelegationTokenIdentifier
name|dtId
init|=
operator|new
name|DelegationTokenIdentifier
argument_list|(
operator|new
name|Text
argument_list|(
literal|"user"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|renewer
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|t
init|=
operator|new
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
argument_list|(
name|dtId
argument_list|,
name|dtSecretManager
argument_list|)
decl_stmt|;
specifier|final
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"hftp://host:2222/file1"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|fs_addr
init|=
name|SecurityUtil
operator|.
name|buildDTServiceName
argument_list|(
name|uri
argument_list|,
name|NameNode
operator|.
name|DEFAULT_PORT
argument_list|)
decl_stmt|;
name|t
operator|.
name|setService
argument_list|(
operator|new
name|Text
argument_list|(
name|fs_addr
argument_list|)
argument_list|)
expr_stmt|;
comment|//when(hfs.getUri()).thenReturn(uri);
name|Mockito
operator|.
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|URI
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|URI
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
return|return
name|uri
return|;
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|hfs
argument_list|)
operator|.
name|getUri
argument_list|()
expr_stmt|;
comment|//when(hfs.getDelegationToken()).thenReturn((Token<? extends TokenIdentifier>) t);
name|Mockito
operator|.
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
return|return
name|t
return|;
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|hfs
argument_list|)
operator|.
name|getDelegationToken
argument_list|(
name|renewer
argument_list|)
expr_stmt|;
comment|//when(hfs.getCanonicalServiceName).thenReturn(fs_addr);
name|Mockito
operator|.
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
return|return
name|fs_addr
return|;
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|hfs
argument_list|)
operator|.
name|getCanonicalServiceName
argument_list|()
expr_stmt|;
name|Credentials
name|credentials
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|uri
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Path for hftp="
operator|+
name|p
operator|+
literal|"; fs_addr="
operator|+
name|fs_addr
operator|+
literal|"; rn="
operator|+
name|renewer
argument_list|)
expr_stmt|;
name|TokenCache
operator|.
name|obtainTokensForNamenodesInternal
argument_list|(
name|hfs
argument_list|,
name|credentials
argument_list|,
name|jConf
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
argument_list|>
name|tns
init|=
name|credentials
operator|.
name|getAllTokens
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"number of tokens is not 1"
argument_list|,
literal|1
argument_list|,
name|tns
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|tt
range|:
name|tns
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"token="
operator|+
name|tt
argument_list|)
expr_stmt|;
if|if
condition|(
name|tt
operator|.
name|getKind
argument_list|()
operator|.
name|equals
argument_list|(
name|DelegationTokenIdentifier
operator|.
name|HDFS_DELEGATION_KIND
argument_list|)
operator|&&
name|tt
operator|.
name|getService
argument_list|()
operator|.
name|equals
argument_list|(
operator|new
name|Text
argument_list|(
name|fs_addr
argument_list|)
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
name|assertEquals
argument_list|(
literal|"different token"
argument_list|,
name|tt
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"didn't find token for "
operator|+
name|p
argument_list|,
name|found
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * verify _HOST substitution    * @throws IOException    */
annotation|@
name|Test
DECL|method|testGetJTPrincipal ()
specifier|public
name|void
name|testGetJTPrincipal
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|serviceName
init|=
literal|"jt/"
decl_stmt|;
name|String
name|hostName
init|=
literal|"foo"
decl_stmt|;
name|String
name|domainName
init|=
literal|"@BAR"
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JTConfig
operator|.
name|JT_IPC_ADDRESS
argument_list|,
name|hostName
operator|+
literal|":8888"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JTConfig
operator|.
name|JT_USER_NAME
argument_list|,
name|serviceName
operator|+
name|SecurityUtil
operator|.
name|HOSTNAME_PATTERN
operator|+
name|domainName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Failed to substitute HOSTNAME_PATTERN with hostName"
argument_list|,
name|serviceName
operator|+
name|hostName
operator|+
name|domainName
argument_list|,
name|TokenCache
operator|.
name|getJTPrincipal
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

