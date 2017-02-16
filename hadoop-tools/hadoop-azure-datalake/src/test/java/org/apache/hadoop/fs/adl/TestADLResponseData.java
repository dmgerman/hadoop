begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.adl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|adl
package|;
end_package

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
name|FileStatus
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_comment
comment|/**  * Mock up response data returned from Adl storage account.  */
end_comment

begin_class
DECL|class|TestADLResponseData
specifier|public
specifier|final
class|class
name|TestADLResponseData
block|{
DECL|method|TestADLResponseData ()
specifier|private
name|TestADLResponseData
parameter_list|()
block|{    }
DECL|method|getGetFileStatusJSONResponse (FileStatus status)
specifier|public
specifier|static
name|String
name|getGetFileStatusJSONResponse
parameter_list|(
name|FileStatus
name|status
parameter_list|)
block|{
return|return
literal|"{\"FileStatus\":{\"length\":"
operator|+
name|status
operator|.
name|getLen
argument_list|()
operator|+
literal|","
operator|+
literal|"\"pathSuffix\":\"\",\"type\":\""
operator|+
operator|(
name|status
operator|.
name|isDirectory
argument_list|()
condition|?
literal|"DIRECTORY"
else|:
literal|"FILE"
operator|)
operator|+
literal|"\""
operator|+
literal|",\"blockSize\":"
operator|+
name|status
operator|.
name|getBlockSize
argument_list|()
operator|+
literal|",\"accessTime\":"
operator|+
name|status
operator|.
name|getAccessTime
argument_list|()
operator|+
literal|",\"modificationTime\":"
operator|+
name|status
operator|.
name|getModificationTime
argument_list|()
operator|+
literal|""
operator|+
literal|",\"replication\":"
operator|+
name|status
operator|.
name|getReplication
argument_list|()
operator|+
literal|",\"permission\":\""
operator|+
name|status
operator|.
name|getPermission
argument_list|()
operator|+
literal|"\",\"owner\":\""
operator|+
name|status
operator|.
name|getOwner
argument_list|()
operator|+
literal|"\",\"group\":\""
operator|+
name|status
operator|.
name|getGroup
argument_list|()
operator|+
literal|"\"}}"
return|;
block|}
DECL|method|getGetFileStatusJSONResponse ()
specifier|public
specifier|static
name|String
name|getGetFileStatusJSONResponse
parameter_list|()
block|{
return|return
name|getGetFileStatusJSONResponse
argument_list|(
literal|4194304
argument_list|)
return|;
block|}
DECL|method|getGetAclStatusJSONResponse ()
specifier|public
specifier|static
name|String
name|getGetAclStatusJSONResponse
parameter_list|()
block|{
return|return
literal|"{\n"
operator|+
literal|"    \"AclStatus\": {\n"
operator|+
literal|"        \"entries\": [\n"
operator|+
literal|"            \"user:carla:rw-\", \n"
operator|+
literal|"            \"group::r-x\"\n"
operator|+
literal|"        ], \n"
operator|+
literal|"        \"group\": \"supergroup\", \n"
operator|+
literal|"        \"owner\": \"hadoop\", \n"
operator|+
literal|"        \"permission\":\"775\",\n"
operator|+
literal|"        \"stickyBit\": false\n"
operator|+
literal|"    }\n"
operator|+
literal|"}"
return|;
block|}
DECL|method|getGetFileStatusJSONResponse (long length)
specifier|public
specifier|static
name|String
name|getGetFileStatusJSONResponse
parameter_list|(
name|long
name|length
parameter_list|)
block|{
return|return
literal|"{\"FileStatus\":{\"length\":"
operator|+
name|length
operator|+
literal|","
operator|+
literal|"\"pathSuffix\":\"\",\"type\":\"FILE\",\"blockSize\":268435456,"
operator|+
literal|"\"accessTime\":1452103827023,\"modificationTime\":1452103827023,"
operator|+
literal|"\"replication\":0,\"permission\":\"777\","
operator|+
literal|"\"owner\":\"NotSupportYet\",\"group\":\"NotSupportYet\"}}"
return|;
block|}
DECL|method|getGetFileStatusJSONResponse (boolean aclBit)
specifier|public
specifier|static
name|String
name|getGetFileStatusJSONResponse
parameter_list|(
name|boolean
name|aclBit
parameter_list|)
block|{
return|return
literal|"{\"FileStatus\":{\"length\":1024,"
operator|+
literal|"\"pathSuffix\":\"\",\"type\":\"FILE\",\"blockSize\":268435456,"
operator|+
literal|"\"accessTime\":1452103827023,\"modificationTime\":1452103827023,"
operator|+
literal|"\"replication\":0,\"permission\":\"777\","
operator|+
literal|"\"owner\":\"NotSupportYet\",\"group\":\"NotSupportYet\",\"aclBit\":\""
operator|+
name|aclBit
operator|+
literal|"\"}}"
return|;
block|}
DECL|method|getListFileStatusJSONResponse (int dirSize)
specifier|public
specifier|static
name|String
name|getListFileStatusJSONResponse
parameter_list|(
name|int
name|dirSize
parameter_list|)
block|{
name|String
name|list
init|=
literal|""
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
name|dirSize
condition|;
operator|++
name|i
control|)
block|{
name|list
operator|+=
literal|"{\"length\":1024,\"pathSuffix\":\""
operator|+
name|java
operator|.
name|util
operator|.
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|+
literal|"\",\"type\":\"FILE\",\"blockSize\":268435456,"
operator|+
literal|"\"accessTime\":1452103878833,"
operator|+
literal|"\"modificationTime\":1452103879190,\"replication\":0,"
operator|+
literal|"\"permission\":\"777\",\"owner\":\"NotSupportYet\","
operator|+
literal|"\"group\":\"NotSupportYet\"},"
expr_stmt|;
block|}
name|list
operator|=
name|list
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|list
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
literal|"{\"FileStatuses\":{\"FileStatus\":["
operator|+
name|list
operator|+
literal|"]}}"
return|;
block|}
DECL|method|getListFileStatusJSONResponse (boolean aclBit)
specifier|public
specifier|static
name|String
name|getListFileStatusJSONResponse
parameter_list|(
name|boolean
name|aclBit
parameter_list|)
block|{
return|return
literal|"{\"FileStatuses\":{\"FileStatus\":[{\"length\":0,\"pathSuffix\":\""
operator|+
name|java
operator|.
name|util
operator|.
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|+
literal|"\",\"type\":\"DIRECTORY\",\"blockSize\":0,"
operator|+
literal|"\"accessTime\":1481184513488,"
operator|+
literal|"\"modificationTime\":1481184513488,\"replication\":0,"
operator|+
literal|"\"permission\":\"770\","
operator|+
literal|"\"owner\":\"4b27fe1a-d9ab-4a04-ad7a-4bba72cd9e6c\","
operator|+
literal|"\"group\":\"4b27fe1a-d9ab-4a04-ad7a-4bba72cd9e6c\",\"aclBit\":\""
operator|+
name|aclBit
operator|+
literal|"\"}]}}"
return|;
block|}
DECL|method|getJSONResponse (boolean status)
specifier|public
specifier|static
name|String
name|getJSONResponse
parameter_list|(
name|boolean
name|status
parameter_list|)
block|{
return|return
literal|"{\"boolean\":"
operator|+
name|status
operator|+
literal|"}"
return|;
block|}
DECL|method|getErrorIllegalArgumentExceptionJSONResponse ()
specifier|public
specifier|static
name|String
name|getErrorIllegalArgumentExceptionJSONResponse
parameter_list|()
block|{
return|return
literal|"{\n"
operator|+
literal|"  \"RemoteException\":\n"
operator|+
literal|"  {\n"
operator|+
literal|"    \"exception\"    : \"IllegalArgumentException\",\n"
operator|+
literal|"    \"javaClassName\": \"java.lang.IllegalArgumentException\",\n"
operator|+
literal|"    \"message\"      : \"Invalid\""
operator|+
literal|"  }\n"
operator|+
literal|"}"
return|;
block|}
DECL|method|getErrorBadOffsetExceptionJSONResponse ()
specifier|public
specifier|static
name|String
name|getErrorBadOffsetExceptionJSONResponse
parameter_list|()
block|{
return|return
literal|"{\n"
operator|+
literal|"  \"RemoteException\":\n"
operator|+
literal|"  {\n"
operator|+
literal|"    \"exception\"    : \"BadOffsetException\",\n"
operator|+
literal|"    \"javaClassName\": \"org.apache.hadoop.fs.adl"
operator|+
literal|".BadOffsetException\",\n"
operator|+
literal|"    \"message\"      : \"Invalid\""
operator|+
literal|"  }\n"
operator|+
literal|"}"
return|;
block|}
DECL|method|getErrorInternalServerExceptionJSONResponse ()
specifier|public
specifier|static
name|String
name|getErrorInternalServerExceptionJSONResponse
parameter_list|()
block|{
return|return
literal|"{\n"
operator|+
literal|"  \"RemoteException\":\n"
operator|+
literal|"  {\n"
operator|+
literal|"    \"exception\"    : \"RuntimeException\",\n"
operator|+
literal|"    \"javaClassName\": \"java.lang.RuntimeException\",\n"
operator|+
literal|"    \"message\"      : \"Internal Server Error\""
operator|+
literal|"  }\n"
operator|+
literal|"}"
return|;
block|}
DECL|method|getAccessControlException ()
specifier|public
specifier|static
name|String
name|getAccessControlException
parameter_list|()
block|{
return|return
literal|"{\n"
operator|+
literal|"  \"RemoteException\":\n"
operator|+
literal|"  {\n"
operator|+
literal|"    \"exception\"    : \"AccessControlException\",\n"
operator|+
literal|"    \"javaClassName\": \"org.apache.hadoop.security"
operator|+
literal|".AccessControlException\",\n"
operator|+
literal|"    \"message\"      : \"Permission denied: ...\"\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
return|;
block|}
DECL|method|getFileNotFoundException ()
specifier|public
specifier|static
name|String
name|getFileNotFoundException
parameter_list|()
block|{
return|return
literal|"{\n"
operator|+
literal|"  \"RemoteException\":\n"
operator|+
literal|"  {\n"
operator|+
literal|"    \"exception\"    : \"FileNotFoundException\",\n"
operator|+
literal|"    \"javaClassName\": \"java.io.FileNotFoundException\",\n"
operator|+
literal|"    \"message\"      : \"File does not exist\"\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
return|;
block|}
DECL|method|getRandomByteArrayData ()
specifier|public
specifier|static
name|byte
index|[]
name|getRandomByteArrayData
parameter_list|()
block|{
return|return
name|getRandomByteArrayData
argument_list|(
literal|4
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
return|;
block|}
DECL|method|getRandomByteArrayData (int size)
specifier|public
specifier|static
name|byte
index|[]
name|getRandomByteArrayData
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|rand
operator|.
name|nextBytes
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
name|b
return|;
block|}
block|}
end_class

end_unit

