begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|java
operator|.
name|util
operator|.
name|StringTokenizer
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
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
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
name|hdfs
operator|.
name|DFSUtil
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
name|HdfsConfiguration
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
name|tools
operator|.
name|GetConf
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
name|tools
operator|.
name|GetConf
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
name|hdfs
operator|.
name|tools
operator|.
name|GetConf
operator|.
name|CommandHandler
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Test for {@link GetConf}  */
end_comment

begin_class
DECL|class|TestGetConf
specifier|public
class|class
name|TestGetConf
block|{
DECL|enum|TestType
enum|enum
name|TestType
block|{
DECL|enumConstant|NAMENODE
DECL|enumConstant|BACKUP
DECL|enumConstant|SECONDARY
DECL|enumConstant|NNRPCADDRESSES
name|NAMENODE
block|,
name|BACKUP
block|,
name|SECONDARY
block|,
name|NNRPCADDRESSES
block|}
comment|/** Setup federation nameServiceIds in the configuration */
DECL|method|setupNameServices (HdfsConfiguration conf, int nameServiceIdCount)
specifier|private
name|void
name|setupNameServices
parameter_list|(
name|HdfsConfiguration
name|conf
parameter_list|,
name|int
name|nameServiceIdCount
parameter_list|)
block|{
name|StringBuilder
name|nsList
init|=
operator|new
name|StringBuilder
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
name|nameServiceIdCount
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|nsList
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|nsList
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|nsList
operator|.
name|append
argument_list|(
name|getNameServiceId
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|DFS_FEDERATION_NAMESERVICES
argument_list|,
name|nsList
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Set a given key with value as address, for all the nameServiceIds.    * @param conf configuration to set the addresses in    * @param key configuration key    * @param nameServiceIdCount Number of nameServices for which the key is set    * @param portOffset starting port offset    * @return list of addresses that are set in the configuration    */
DECL|method|setupAddress (HdfsConfiguration conf, String key, int nameServiceIdCount, int portOffset)
specifier|private
name|String
index|[]
name|setupAddress
parameter_list|(
name|HdfsConfiguration
name|conf
parameter_list|,
name|String
name|key
parameter_list|,
name|int
name|nameServiceIdCount
parameter_list|,
name|int
name|portOffset
parameter_list|)
block|{
name|String
index|[]
name|values
init|=
operator|new
name|String
index|[
name|nameServiceIdCount
index|]
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
name|nameServiceIdCount
condition|;
name|i
operator|++
operator|,
name|portOffset
operator|++
control|)
block|{
name|String
name|nsID
init|=
name|getNameServiceId
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|specificKey
init|=
name|DFSUtil
operator|.
name|getNameServiceIdKey
argument_list|(
name|key
argument_list|,
name|nsID
argument_list|)
decl_stmt|;
name|values
index|[
name|i
index|]
operator|=
literal|"nn"
operator|+
name|i
operator|+
literal|":"
operator|+
name|portOffset
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|specificKey
argument_list|,
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|values
return|;
block|}
comment|/*    * Convert list of InetSocketAddress to string array with each address    * represented as "host:port"    */
DECL|method|toStringArray (List<InetSocketAddress> list)
specifier|private
name|String
index|[]
name|toStringArray
parameter_list|(
name|List
argument_list|<
name|InetSocketAddress
argument_list|>
name|list
parameter_list|)
block|{
name|String
index|[]
name|ret
init|=
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
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
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ret
index|[
name|i
index|]
operator|=
name|NameNode
operator|.
name|getHostPortString
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|/**    * Using DFSUtil methods get the list of given {@code type} of address    */
DECL|method|getAddressListFromConf (TestType type, HdfsConfiguration conf)
specifier|private
name|List
argument_list|<
name|InetSocketAddress
argument_list|>
name|getAddressListFromConf
parameter_list|(
name|TestType
name|type
parameter_list|,
name|HdfsConfiguration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|NAMENODE
case|:
return|return
name|DFSUtil
operator|.
name|getNNServiceRpcAddresses
argument_list|(
name|conf
argument_list|)
return|;
case|case
name|BACKUP
case|:
return|return
name|DFSUtil
operator|.
name|getBackupNodeAddresses
argument_list|(
name|conf
argument_list|)
return|;
case|case
name|SECONDARY
case|:
return|return
name|DFSUtil
operator|.
name|getSecondaryNameNodeAddresses
argument_list|(
name|conf
argument_list|)
return|;
case|case
name|NNRPCADDRESSES
case|:
return|return
name|DFSUtil
operator|.
name|getNNServiceRpcAddresses
argument_list|(
name|conf
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|runTool (HdfsConfiguration conf, String[] args, boolean success)
specifier|private
name|String
name|runTool
parameter_list|(
name|HdfsConfiguration
name|conf
parameter_list|,
name|String
index|[]
name|args
parameter_list|,
name|boolean
name|success
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|o
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|out
init|=
operator|new
name|PrintStream
argument_list|(
name|o
argument_list|,
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|ret
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|GetConf
argument_list|(
name|conf
argument_list|,
name|out
argument_list|,
name|out
argument_list|)
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|success
argument_list|,
name|ret
operator|==
literal|0
argument_list|)
expr_stmt|;
return|return
name|o
operator|.
name|toString
argument_list|()
return|;
block|}
finally|finally
block|{
name|o
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Get address list for a given type of address. Command expected to    * fail if {@code success} is false.    * @return returns the success or error output from the tool.    */
DECL|method|getAddressListFromTool (TestType type, HdfsConfiguration conf, boolean success)
specifier|private
name|String
name|getAddressListFromTool
parameter_list|(
name|TestType
name|type
parameter_list|,
name|HdfsConfiguration
name|conf
parameter_list|,
name|boolean
name|success
parameter_list|)
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[
literal|1
index|]
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|NAMENODE
case|:
name|args
index|[
literal|0
index|]
operator|=
name|Command
operator|.
name|NAMENODE
operator|.
name|getName
argument_list|()
expr_stmt|;
break|break;
case|case
name|BACKUP
case|:
name|args
index|[
literal|0
index|]
operator|=
name|Command
operator|.
name|BACKUP
operator|.
name|getName
argument_list|()
expr_stmt|;
break|break;
case|case
name|SECONDARY
case|:
name|args
index|[
literal|0
index|]
operator|=
name|Command
operator|.
name|SECONDARY
operator|.
name|getName
argument_list|()
expr_stmt|;
break|break;
case|case
name|NNRPCADDRESSES
case|:
name|args
index|[
literal|0
index|]
operator|=
name|Command
operator|.
name|NNRPCADDRESSES
operator|.
name|getName
argument_list|()
expr_stmt|;
break|break;
block|}
return|return
name|runTool
argument_list|(
name|conf
argument_list|,
name|args
argument_list|,
name|success
argument_list|)
return|;
block|}
comment|/**    * Using {@link GetConf} methods get the list of given {@code type} of    * addresses    *     * @param type, TestType    * @param conf, configuration    * @param checkPort, If checkPort is true, verify NNPRCADDRESSES whose     *      expected value is hostname:rpc-port.  If checkPort is false, the     *      expected is hostname only.    * @param expected, expected addresses    */
DECL|method|getAddressListFromTool (TestType type, HdfsConfiguration conf, boolean checkPort, List<InetSocketAddress> expected)
specifier|private
name|void
name|getAddressListFromTool
parameter_list|(
name|TestType
name|type
parameter_list|,
name|HdfsConfiguration
name|conf
parameter_list|,
name|boolean
name|checkPort
parameter_list|,
name|List
argument_list|<
name|InetSocketAddress
argument_list|>
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|out
init|=
name|getAddressListFromTool
argument_list|(
name|type
argument_list|,
name|conf
argument_list|,
name|expected
operator|.
name|size
argument_list|()
operator|!=
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// Convert list of addresses returned to an array of string
name|StringTokenizer
name|tokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|out
argument_list|)
decl_stmt|;
while|while
condition|(
name|tokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|s
init|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|values
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|actual
init|=
name|values
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|values
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
comment|// Convert expected list to String[] of hosts
name|int
name|i
init|=
literal|0
decl_stmt|;
name|String
index|[]
name|expectedHosts
init|=
operator|new
name|String
index|[
name|expected
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|InetSocketAddress
name|addr
range|:
name|expected
control|)
block|{
if|if
condition|(
operator|!
name|checkPort
condition|)
block|{
name|expectedHosts
index|[
name|i
operator|++
index|]
operator|=
name|addr
operator|.
name|getHostName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|expectedHosts
index|[
name|i
operator|++
index|]
operator|=
name|addr
operator|.
name|getHostName
argument_list|()
operator|+
literal|":"
operator|+
name|addr
operator|.
name|getPort
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Compare two arrays
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|expectedHosts
argument_list|,
name|actual
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyAddresses (HdfsConfiguration conf, TestType type, boolean checkPort, String... expected)
specifier|private
name|void
name|verifyAddresses
parameter_list|(
name|HdfsConfiguration
name|conf
parameter_list|,
name|TestType
name|type
parameter_list|,
name|boolean
name|checkPort
parameter_list|,
name|String
modifier|...
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Ensure DFSUtil returned the right set of addresses
name|List
argument_list|<
name|InetSocketAddress
argument_list|>
name|list
init|=
name|getAddressListFromConf
argument_list|(
name|type
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|String
index|[]
name|actual
init|=
name|toStringArray
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|actual
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test GetConf returned addresses
name|getAddressListFromTool
argument_list|(
name|type
argument_list|,
name|conf
argument_list|,
name|checkPort
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
DECL|method|getNameServiceId (int index)
specifier|private
specifier|static
name|String
name|getNameServiceId
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
literal|"ns"
operator|+
name|index
return|;
block|}
comment|/**    * Test empty configuration    */
annotation|@
name|Test
DECL|method|testEmptyConf ()
specifier|public
name|void
name|testEmptyConf
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|// Verify getting addresses fails
name|getAddressListFromTool
argument_list|(
name|TestType
operator|.
name|NAMENODE
argument_list|,
name|conf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|getAddressListFromTool
argument_list|(
name|TestType
operator|.
name|BACKUP
argument_list|,
name|conf
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|getAddressListFromTool
argument_list|(
name|TestType
operator|.
name|SECONDARY
argument_list|,
name|conf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|getAddressListFromTool
argument_list|(
name|TestType
operator|.
name|NNRPCADDRESSES
argument_list|,
name|conf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|Command
name|cmd
range|:
name|Command
operator|.
name|values
argument_list|()
control|)
block|{
name|CommandHandler
name|handler
init|=
name|Command
operator|.
name|getHandler
argument_list|(
name|cmd
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|.
name|key
operator|!=
literal|null
condition|)
block|{
comment|// First test with configuration missing the required key
name|String
index|[]
name|args
init|=
block|{
name|handler
operator|.
name|key
block|}
decl_stmt|;
name|runTool
argument_list|(
name|conf
argument_list|,
name|args
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Test invalid argument to the tool    */
annotation|@
name|Test
DECL|method|testInvalidArgument ()
specifier|public
name|void
name|testInvalidArgument
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|String
index|[]
name|args
init|=
block|{
literal|"-invalidArgument"
block|}
decl_stmt|;
name|String
name|ret
init|=
name|runTool
argument_list|(
name|conf
argument_list|,
name|args
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ret
operator|.
name|contains
argument_list|(
name|GetConf
operator|.
name|USAGE
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests to make sure the returned addresses are correct in case of default    * configuration with no federation    */
annotation|@
name|Test
DECL|method|testNonFederation ()
specifier|public
name|void
name|testNonFederation
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|// Returned namenode address should match default address
name|conf
operator|.
name|set
argument_list|(
name|FS_DEFAULT_NAME_KEY
argument_list|,
literal|"hdfs://localhost:1000"
argument_list|)
expr_stmt|;
name|verifyAddresses
argument_list|(
name|conf
argument_list|,
name|TestType
operator|.
name|NAMENODE
argument_list|,
literal|false
argument_list|,
literal|"localhost:1000"
argument_list|)
expr_stmt|;
name|verifyAddresses
argument_list|(
name|conf
argument_list|,
name|TestType
operator|.
name|NNRPCADDRESSES
argument_list|,
literal|true
argument_list|,
literal|"localhost:1000"
argument_list|)
expr_stmt|;
comment|// Returned address should match backupnode RPC address
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_BACKUP_ADDRESS_KEY
argument_list|,
literal|"localhost:1001"
argument_list|)
expr_stmt|;
name|verifyAddresses
argument_list|(
name|conf
argument_list|,
name|TestType
operator|.
name|BACKUP
argument_list|,
literal|false
argument_list|,
literal|"localhost:1001"
argument_list|)
expr_stmt|;
comment|// Returned address should match secondary http address
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY
argument_list|,
literal|"localhost:1002"
argument_list|)
expr_stmt|;
name|verifyAddresses
argument_list|(
name|conf
argument_list|,
name|TestType
operator|.
name|SECONDARY
argument_list|,
literal|false
argument_list|,
literal|"localhost:1002"
argument_list|)
expr_stmt|;
comment|// Returned namenode address should match service RPC address
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_SERVICE_RPC_ADDRESS_KEY
argument_list|,
literal|"localhost:1000"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|,
literal|"localhost:1001"
argument_list|)
expr_stmt|;
name|verifyAddresses
argument_list|(
name|conf
argument_list|,
name|TestType
operator|.
name|NAMENODE
argument_list|,
literal|false
argument_list|,
literal|"localhost:1000"
argument_list|)
expr_stmt|;
name|verifyAddresses
argument_list|(
name|conf
argument_list|,
name|TestType
operator|.
name|NNRPCADDRESSES
argument_list|,
literal|true
argument_list|,
literal|"localhost:1000"
argument_list|)
expr_stmt|;
comment|// Returned address should match RPC address
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|,
literal|"localhost:1001"
argument_list|)
expr_stmt|;
name|verifyAddresses
argument_list|(
name|conf
argument_list|,
name|TestType
operator|.
name|NAMENODE
argument_list|,
literal|false
argument_list|,
literal|"localhost:1001"
argument_list|)
expr_stmt|;
name|verifyAddresses
argument_list|(
name|conf
argument_list|,
name|TestType
operator|.
name|NNRPCADDRESSES
argument_list|,
literal|true
argument_list|,
literal|"localhost:1001"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests to make sure the returned addresses are correct in case of federation    * of setup.    */
annotation|@
name|Test
DECL|method|testFederation ()
specifier|public
name|void
name|testFederation
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|nsCount
init|=
literal|10
decl_stmt|;
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|// Test to ensure namenode, backup and secondary namenode addresses are
comment|// returned from federation configuration. Returned namenode addresses are
comment|// based on service RPC address and not regular RPC address
name|setupNameServices
argument_list|(
name|conf
argument_list|,
name|nsCount
argument_list|)
expr_stmt|;
name|String
index|[]
name|nnAddresses
init|=
name|setupAddress
argument_list|(
name|conf
argument_list|,
name|DFS_NAMENODE_SERVICE_RPC_ADDRESS_KEY
argument_list|,
name|nsCount
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|setupAddress
argument_list|(
name|conf
argument_list|,
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|,
name|nsCount
argument_list|,
literal|1500
argument_list|)
expr_stmt|;
name|String
index|[]
name|backupAddresses
init|=
name|setupAddress
argument_list|(
name|conf
argument_list|,
name|DFS_NAMENODE_BACKUP_ADDRESS_KEY
argument_list|,
name|nsCount
argument_list|,
literal|2000
argument_list|)
decl_stmt|;
name|String
index|[]
name|secondaryAddresses
init|=
name|setupAddress
argument_list|(
name|conf
argument_list|,
name|DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY
argument_list|,
name|nsCount
argument_list|,
literal|3000
argument_list|)
decl_stmt|;
name|verifyAddresses
argument_list|(
name|conf
argument_list|,
name|TestType
operator|.
name|NAMENODE
argument_list|,
literal|false
argument_list|,
name|nnAddresses
argument_list|)
expr_stmt|;
name|verifyAddresses
argument_list|(
name|conf
argument_list|,
name|TestType
operator|.
name|BACKUP
argument_list|,
literal|false
argument_list|,
name|backupAddresses
argument_list|)
expr_stmt|;
name|verifyAddresses
argument_list|(
name|conf
argument_list|,
name|TestType
operator|.
name|SECONDARY
argument_list|,
literal|false
argument_list|,
name|secondaryAddresses
argument_list|)
expr_stmt|;
name|verifyAddresses
argument_list|(
name|conf
argument_list|,
name|TestType
operator|.
name|NNRPCADDRESSES
argument_list|,
literal|true
argument_list|,
name|nnAddresses
argument_list|)
expr_stmt|;
comment|// Test to ensure namenode, backup, secondary namenode addresses and
comment|// namenode rpc addresses are  returned from federation configuration.
comment|// Returned namenode addresses are based on regular RPC address
comment|// in the absence of service RPC address.
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setupNameServices
argument_list|(
name|conf
argument_list|,
name|nsCount
argument_list|)
expr_stmt|;
name|nnAddresses
operator|=
name|setupAddress
argument_list|(
name|conf
argument_list|,
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|,
name|nsCount
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|backupAddresses
operator|=
name|setupAddress
argument_list|(
name|conf
argument_list|,
name|DFS_NAMENODE_BACKUP_ADDRESS_KEY
argument_list|,
name|nsCount
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
name|secondaryAddresses
operator|=
name|setupAddress
argument_list|(
name|conf
argument_list|,
name|DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY
argument_list|,
name|nsCount
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
name|verifyAddresses
argument_list|(
name|conf
argument_list|,
name|TestType
operator|.
name|NAMENODE
argument_list|,
literal|false
argument_list|,
name|nnAddresses
argument_list|)
expr_stmt|;
name|verifyAddresses
argument_list|(
name|conf
argument_list|,
name|TestType
operator|.
name|BACKUP
argument_list|,
literal|false
argument_list|,
name|backupAddresses
argument_list|)
expr_stmt|;
name|verifyAddresses
argument_list|(
name|conf
argument_list|,
name|TestType
operator|.
name|SECONDARY
argument_list|,
literal|false
argument_list|,
name|secondaryAddresses
argument_list|)
expr_stmt|;
name|verifyAddresses
argument_list|(
name|conf
argument_list|,
name|TestType
operator|.
name|NNRPCADDRESSES
argument_list|,
literal|true
argument_list|,
name|nnAddresses
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests commands other than {@link Command#NAMENODE}, {@link Command#BACKUP},    * {@link Command#SECONDARY} and {@link Command#NNRPCADDRESSES}    */
DECL|method|testTool ()
specifier|public
name|void
name|testTool
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
for|for
control|(
name|Command
name|cmd
range|:
name|Command
operator|.
name|values
argument_list|()
control|)
block|{
name|CommandHandler
name|handler
init|=
name|Command
operator|.
name|getHandler
argument_list|(
name|cmd
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|.
name|key
operator|!=
literal|null
condition|)
block|{
comment|// Add the key to the conf and ensure tool returns the right value
name|String
index|[]
name|args
init|=
block|{
name|handler
operator|.
name|key
block|}
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|handler
operator|.
name|key
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|runTool
argument_list|(
name|conf
argument_list|,
name|args
argument_list|,
literal|true
argument_list|)
operator|.
name|contains
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

