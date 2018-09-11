begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.ozShell.keys
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|web
operator|.
name|ozShell
operator|.
name|keys
package|;
end_package

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
name|FileInputStream
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
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|hdds
operator|.
name|client
operator|.
name|ReplicationFactor
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
name|hdds
operator|.
name|client
operator|.
name|ReplicationType
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|IOUtils
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
name|ozone
operator|.
name|client
operator|.
name|OzoneBucket
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
name|ozone
operator|.
name|client
operator|.
name|OzoneClientException
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
name|ozone
operator|.
name|client
operator|.
name|OzoneVolume
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
name|ozone
operator|.
name|client
operator|.
name|io
operator|.
name|OzoneOutputStream
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
name|ozone
operator|.
name|web
operator|.
name|ozShell
operator|.
name|Handler
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
name|ozone
operator|.
name|web
operator|.
name|ozShell
operator|.
name|Shell
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
name|digest
operator|.
name|DigestUtils
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CHUNK_SIZE_DEFAULT
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CHUNK_SIZE_KEY
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_REPLICATION
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_REPLICATION_DEFAULT
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_REPLICATION_TYPE
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_REPLICATION_TYPE_DEFAULT
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|Command
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|Parameters
import|;
end_import

begin_comment
comment|/**  * Puts a file into an ozone bucket.  */
end_comment

begin_class
annotation|@
name|Command
argument_list|(
name|name
operator|=
literal|"put"
argument_list|,
name|description
operator|=
literal|"creates or overwrites an existing key"
argument_list|)
DECL|class|PutKeyHandler
specifier|public
class|class
name|PutKeyHandler
extends|extends
name|Handler
block|{
annotation|@
name|Parameters
argument_list|(
name|index
operator|=
literal|"0"
argument_list|,
name|arity
operator|=
literal|"1..1"
argument_list|,
name|description
operator|=
name|Shell
operator|.
name|OZONE_KEY_URI_DESCRIPTION
argument_list|)
DECL|field|uri
specifier|private
name|String
name|uri
decl_stmt|;
annotation|@
name|Parameters
argument_list|(
name|index
operator|=
literal|"1"
argument_list|,
name|arity
operator|=
literal|"1..1"
argument_list|,
name|description
operator|=
literal|"File to upload"
argument_list|)
DECL|field|fileName
specifier|private
name|String
name|fileName
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"-r"
block|,
literal|"--replication"
block|}
argument_list|,
name|description
operator|=
literal|"Replication factor of the new key. (use ONE or THREE) "
operator|+
literal|"Default is specified in the cluster-wide config."
argument_list|)
DECL|field|replicationFactor
specifier|private
name|ReplicationFactor
name|replicationFactor
decl_stmt|;
comment|/**    * Executes the Client Calls.    */
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|URI
name|ozoneURI
init|=
name|verifyURI
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|Path
name|path
init|=
name|Paths
operator|.
name|get
argument_list|(
name|ozoneURI
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|getNameCount
argument_list|()
operator|<
literal|3
condition|)
block|{
throw|throw
operator|new
name|OzoneClientException
argument_list|(
literal|"volume/bucket/key name required in putKey"
argument_list|)
throw|;
block|}
name|String
name|volumeName
init|=
name|path
operator|.
name|getName
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|bucketName
init|=
name|path
operator|.
name|getName
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|keyName
init|=
name|path
operator|.
name|getName
argument_list|(
literal|2
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|isVerbose
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Volume Name : %s%n"
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Bucket Name : %s%n"
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Key Name : %s%n"
argument_list|,
name|keyName
argument_list|)
expr_stmt|;
block|}
name|File
name|dataFile
init|=
operator|new
name|File
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|isVerbose
argument_list|()
condition|)
block|{
name|FileInputStream
name|stream
init|=
operator|new
name|FileInputStream
argument_list|(
name|dataFile
argument_list|)
decl_stmt|;
name|String
name|hash
init|=
name|DigestUtils
operator|.
name|md5Hex
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"File Hash : %s%n"
argument_list|,
name|hash
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
if|if
condition|(
name|replicationFactor
operator|==
literal|null
condition|)
block|{
name|replicationFactor
operator|=
name|ReplicationFactor
operator|.
name|valueOf
argument_list|(
name|conf
operator|.
name|getInt
argument_list|(
name|OZONE_REPLICATION
argument_list|,
name|OZONE_REPLICATION_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ReplicationType
name|replicationType
init|=
name|ReplicationType
operator|.
name|valueOf
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|OZONE_REPLICATION_TYPE
argument_list|,
name|OZONE_REPLICATION_TYPE_DEFAULT
argument_list|)
argument_list|)
decl_stmt|;
name|OzoneVolume
name|vol
init|=
name|client
operator|.
name|getObjectStore
argument_list|()
operator|.
name|getVolume
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|OzoneBucket
name|bucket
init|=
name|vol
operator|.
name|getBucket
argument_list|(
name|bucketName
argument_list|)
decl_stmt|;
name|OzoneOutputStream
name|outputStream
init|=
name|bucket
operator|.
name|createKey
argument_list|(
name|keyName
argument_list|,
name|dataFile
operator|.
name|length
argument_list|()
argument_list|,
name|replicationType
argument_list|,
name|replicationFactor
argument_list|)
decl_stmt|;
name|FileInputStream
name|fileInputStream
init|=
operator|new
name|FileInputStream
argument_list|(
name|dataFile
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|fileInputStream
argument_list|,
name|outputStream
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|OZONE_SCM_CHUNK_SIZE_KEY
argument_list|,
name|OZONE_SCM_CHUNK_SIZE_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|outputStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|fileInputStream
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

