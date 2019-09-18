begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.freon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|freon
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|concurrent
operator|.
name|Callable
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
name|cli
operator|.
name|HddsVersionProvider
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
name|OzoneClient
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
name|OzoneClientFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Timer
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

begin_comment
comment|/**  * Data generator tool test om performance.  */
end_comment

begin_class
annotation|@
name|Command
argument_list|(
name|name
operator|=
literal|"ockg"
argument_list|,
name|aliases
operator|=
literal|"ozone-client-key-generator"
argument_list|,
name|description
operator|=
literal|"Generate keys with the help of the ozone clients."
argument_list|,
name|versionProvider
operator|=
name|HddsVersionProvider
operator|.
name|class
argument_list|,
name|mixinStandardHelpOptions
operator|=
literal|true
argument_list|,
name|showDefaultValues
operator|=
literal|true
argument_list|)
DECL|class|OzoneClientKeyGenerator
specifier|public
class|class
name|OzoneClientKeyGenerator
extends|extends
name|BaseFreonGenerator
implements|implements
name|Callable
argument_list|<
name|Void
argument_list|>
block|{
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"-v"
block|,
literal|"--volume"
block|}
argument_list|,
name|description
operator|=
literal|"Name of the bucket which contains the test data. Will be"
operator|+
literal|" created if missing."
argument_list|,
name|defaultValue
operator|=
literal|"vol1"
argument_list|)
DECL|field|volumeName
specifier|private
name|String
name|volumeName
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"-b"
block|,
literal|"--bucket"
block|}
argument_list|,
name|description
operator|=
literal|"Name of the bucket which contains the test data. Will be"
operator|+
literal|" created if missing."
argument_list|,
name|defaultValue
operator|=
literal|"bucket1"
argument_list|)
DECL|field|bucketName
specifier|private
name|String
name|bucketName
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"-s"
block|,
literal|"--size"
block|}
argument_list|,
name|description
operator|=
literal|"Size of the generated key (in bytes)"
argument_list|,
name|defaultValue
operator|=
literal|"10240"
argument_list|)
DECL|field|keySize
specifier|private
name|int
name|keySize
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"--buffer"
block|}
argument_list|,
name|description
operator|=
literal|"Size of buffer used to generated the key content."
argument_list|,
name|defaultValue
operator|=
literal|"4096"
argument_list|)
DECL|field|bufferSize
specifier|private
name|int
name|bufferSize
decl_stmt|;
DECL|field|timer
specifier|private
name|Timer
name|timer
decl_stmt|;
DECL|field|bucket
specifier|private
name|OzoneBucket
name|bucket
decl_stmt|;
DECL|field|contentGenerator
specifier|private
name|ContentGenerator
name|contentGenerator
decl_stmt|;
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
name|init
argument_list|()
expr_stmt|;
name|OzoneConfiguration
name|ozoneConfiguration
init|=
name|createOzoneConfiguration
argument_list|()
decl_stmt|;
name|ensureVolumeAndBucketExist
argument_list|(
name|ozoneConfiguration
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
name|contentGenerator
operator|=
operator|new
name|ContentGenerator
argument_list|(
name|keySize
argument_list|,
name|bufferSize
argument_list|)
expr_stmt|;
try|try
init|(
name|OzoneClient
name|rpcClient
init|=
name|OzoneClientFactory
operator|.
name|getRpcClient
argument_list|(
name|ozoneConfiguration
argument_list|)
init|)
block|{
name|bucket
operator|=
name|rpcClient
operator|.
name|getObjectStore
argument_list|()
operator|.
name|getVolume
argument_list|(
name|volumeName
argument_list|)
operator|.
name|getBucket
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|timer
operator|=
name|getMetrics
argument_list|()
operator|.
name|timer
argument_list|(
literal|"key-create"
argument_list|)
expr_stmt|;
name|runTests
argument_list|(
name|this
operator|::
name|createKey
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|createKey (long counter)
specifier|private
name|void
name|createKey
parameter_list|(
name|long
name|counter
parameter_list|)
throws|throws
name|Exception
block|{
name|timer
operator|.
name|time
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
init|(
name|OutputStream
name|stream
init|=
name|bucket
operator|.
name|createKey
argument_list|(
name|generateObjectName
argument_list|(
name|counter
argument_list|)
argument_list|,
name|keySize
argument_list|,
name|ReplicationType
operator|.
name|RATIS
argument_list|,
name|ReplicationFactor
operator|.
name|THREE
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
init|)
block|{
name|contentGenerator
operator|.
name|write
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

