begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.aliasmap
package|package
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
name|aliasmap
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|InvalidProtocolBufferException
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
name|compress
operator|.
name|archivers
operator|.
name|tar
operator|.
name|TarArchiveEntry
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
name|compress
operator|.
name|archivers
operator|.
name|tar
operator|.
name|TarArchiveOutputStream
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
name|compress
operator|.
name|compressors
operator|.
name|gzip
operator|.
name|GzipCompressorOutputStream
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
name|classification
operator|.
name|InterfaceStability
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
name|Configurable
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
name|FileUtil
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
name|DFSConfigKeys
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
name|protocol
operator|.
name|Block
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
name|protocol
operator|.
name|ProvidedStorageLocation
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
name|protocol
operator|.
name|proto
operator|.
name|HdfsProtos
operator|.
name|BlockProto
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
name|protocol
operator|.
name|proto
operator|.
name|HdfsProtos
operator|.
name|ProvidedStorageLocationProto
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
name|protocolPB
operator|.
name|PBHelperClient
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
name|common
operator|.
name|FileRegion
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
name|ImageServlet
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
name|TransferFsImage
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
name|util
operator|.
name|DataTransferThrottler
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
name|fusesource
operator|.
name|leveldbjni
operator|.
name|JniDBFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|DB
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|DBIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|Options
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|ReadOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|Snapshot
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

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
import|;
end_import

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
name|io
operator|.
name|FileOutputStream
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_comment
comment|/**  * InMemoryAliasMap is an implementation of the InMemoryAliasMapProtocol for  * use with LevelDB.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|InMemoryAliasMap
specifier|public
class|class
name|InMemoryAliasMap
implements|implements
name|InMemoryAliasMapProtocol
implements|,
name|Configurable
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
name|InMemoryAliasMap
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SNAPSHOT_COPY_DIR
specifier|private
specifier|static
specifier|final
name|String
name|SNAPSHOT_COPY_DIR
init|=
literal|"aliasmap_snapshot"
decl_stmt|;
DECL|field|TAR_NAME
specifier|private
specifier|static
specifier|final
name|String
name|TAR_NAME
init|=
literal|"aliasmap.tar.gz"
decl_stmt|;
DECL|field|aliasMapURI
specifier|private
specifier|final
name|URI
name|aliasMapURI
decl_stmt|;
DECL|field|levelDb
specifier|private
specifier|final
name|DB
name|levelDb
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|blockPoolID
specifier|private
name|String
name|blockPoolID
decl_stmt|;
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|this
operator|.
name|conf
return|;
block|}
DECL|method|init (Configuration conf, String blockPoolID)
specifier|public
specifier|static
annotation|@
name|Nonnull
name|InMemoryAliasMap
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|blockPoolID
parameter_list|)
throws|throws
name|IOException
block|{
name|Options
name|options
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|options
operator|.
name|createIfMissing
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|String
name|directory
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_PROVIDED_ALIASMAP_INMEMORY_LEVELDB_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|directory
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"InMemoryAliasMap location is null"
argument_list|)
throw|;
block|}
name|File
name|levelDBpath
decl_stmt|;
if|if
condition|(
name|blockPoolID
operator|!=
literal|null
condition|)
block|{
name|levelDBpath
operator|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|blockPoolID
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|levelDBpath
operator|=
operator|new
name|File
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|levelDBpath
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"InMemoryAliasMap location {} is missing. Creating it."
argument_list|,
name|levelDBpath
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|levelDBpath
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create missing aliasmap location: "
operator|+
name|levelDBpath
argument_list|)
throw|;
block|}
block|}
name|DB
name|levelDb
init|=
name|JniDBFactory
operator|.
name|factory
operator|.
name|open
argument_list|(
name|levelDBpath
argument_list|,
name|options
argument_list|)
decl_stmt|;
name|InMemoryAliasMap
name|aliasMap
init|=
operator|new
name|InMemoryAliasMap
argument_list|(
name|levelDBpath
operator|.
name|toURI
argument_list|()
argument_list|,
name|levelDb
argument_list|,
name|blockPoolID
argument_list|)
decl_stmt|;
name|aliasMap
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
name|aliasMap
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|InMemoryAliasMap (URI aliasMapURI, DB levelDb, String blockPoolID)
name|InMemoryAliasMap
parameter_list|(
name|URI
name|aliasMapURI
parameter_list|,
name|DB
name|levelDb
parameter_list|,
name|String
name|blockPoolID
parameter_list|)
block|{
name|this
operator|.
name|aliasMapURI
operator|=
name|aliasMapURI
expr_stmt|;
name|this
operator|.
name|levelDb
operator|=
name|levelDb
expr_stmt|;
name|this
operator|.
name|blockPoolID
operator|=
name|blockPoolID
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|list (Optional<Block> marker)
specifier|public
name|IterationResult
name|list
parameter_list|(
name|Optional
argument_list|<
name|Block
argument_list|>
name|marker
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|DBIterator
name|iterator
init|=
name|levelDb
operator|.
name|iterator
argument_list|()
init|)
block|{
name|Integer
name|batchSize
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_PROVIDED_ALIASMAP_INMEMORY_BATCH_SIZE
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_PROVIDED_ALIASMAP_INMEMORY_BATCH_SIZE_DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
name|marker
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|iterator
operator|.
name|seek
argument_list|(
name|toProtoBufBytes
argument_list|(
name|marker
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|iterator
operator|.
name|seekToFirst
argument_list|()
expr_stmt|;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
name|ArrayList
argument_list|<
name|FileRegion
argument_list|>
name|batch
init|=
name|Lists
operator|.
name|newArrayListWithExpectedSize
argument_list|(
name|batchSize
argument_list|)
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
operator|&&
name|i
operator|<
name|batchSize
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|Block
name|block
init|=
name|fromBlockBytes
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|ProvidedStorageLocation
name|providedStorageLocation
init|=
name|fromProvidedStorageLocationBytes
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|batch
operator|.
name|add
argument_list|(
operator|new
name|FileRegion
argument_list|(
name|block
argument_list|,
name|providedStorageLocation
argument_list|)
argument_list|)
expr_stmt|;
operator|++
name|i
expr_stmt|;
block|}
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Block
name|nextMarker
init|=
name|fromBlockBytes
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|IterationResult
argument_list|(
name|batch
argument_list|,
name|Optional
operator|.
name|of
argument_list|(
name|nextMarker
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|IterationResult
argument_list|(
name|batch
argument_list|,
name|Optional
operator|.
name|empty
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
DECL|method|read (@onnull Block block)
specifier|public
annotation|@
name|Nonnull
name|Optional
argument_list|<
name|ProvidedStorageLocation
argument_list|>
name|read
parameter_list|(
annotation|@
name|Nonnull
name|Block
name|block
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|extendedBlockDbFormat
init|=
name|toProtoBufBytes
argument_list|(
name|block
argument_list|)
decl_stmt|;
name|byte
index|[]
name|providedStorageLocationDbFormat
init|=
name|levelDb
operator|.
name|get
argument_list|(
name|extendedBlockDbFormat
argument_list|)
decl_stmt|;
if|if
condition|(
name|providedStorageLocationDbFormat
operator|==
literal|null
condition|)
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
else|else
block|{
name|ProvidedStorageLocation
name|providedStorageLocation
init|=
name|fromProvidedStorageLocationBytes
argument_list|(
name|providedStorageLocationDbFormat
argument_list|)
decl_stmt|;
return|return
name|Optional
operator|.
name|of
argument_list|(
name|providedStorageLocation
argument_list|)
return|;
block|}
block|}
DECL|method|write (@onnull Block block, @Nonnull ProvidedStorageLocation providedStorageLocation)
specifier|public
name|void
name|write
parameter_list|(
annotation|@
name|Nonnull
name|Block
name|block
parameter_list|,
annotation|@
name|Nonnull
name|ProvidedStorageLocation
name|providedStorageLocation
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|extendedBlockDbFormat
init|=
name|toProtoBufBytes
argument_list|(
name|block
argument_list|)
decl_stmt|;
name|byte
index|[]
name|providedStorageLocationDbFormat
init|=
name|toProtoBufBytes
argument_list|(
name|providedStorageLocation
argument_list|)
decl_stmt|;
name|levelDb
operator|.
name|put
argument_list|(
name|extendedBlockDbFormat
argument_list|,
name|providedStorageLocationDbFormat
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBlockPoolId ()
specifier|public
name|String
name|getBlockPoolId
parameter_list|()
block|{
return|return
name|blockPoolID
return|;
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|levelDb
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Nonnull
DECL|method|fromProvidedStorageLocationBytes ( @onnull byte[] providedStorageLocationDbFormat)
specifier|public
specifier|static
name|ProvidedStorageLocation
name|fromProvidedStorageLocationBytes
parameter_list|(
annotation|@
name|Nonnull
name|byte
index|[]
name|providedStorageLocationDbFormat
parameter_list|)
throws|throws
name|InvalidProtocolBufferException
block|{
name|ProvidedStorageLocationProto
name|providedStorageLocationProto
init|=
name|ProvidedStorageLocationProto
operator|.
name|parseFrom
argument_list|(
name|providedStorageLocationDbFormat
argument_list|)
decl_stmt|;
return|return
name|PBHelperClient
operator|.
name|convert
argument_list|(
name|providedStorageLocationProto
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
DECL|method|fromBlockBytes (@onnull byte[] blockDbFormat)
specifier|public
specifier|static
name|Block
name|fromBlockBytes
parameter_list|(
annotation|@
name|Nonnull
name|byte
index|[]
name|blockDbFormat
parameter_list|)
throws|throws
name|InvalidProtocolBufferException
block|{
name|BlockProto
name|blockProto
init|=
name|BlockProto
operator|.
name|parseFrom
argument_list|(
name|blockDbFormat
argument_list|)
decl_stmt|;
return|return
name|PBHelperClient
operator|.
name|convert
argument_list|(
name|blockProto
argument_list|)
return|;
block|}
DECL|method|toProtoBufBytes (@onnull ProvidedStorageLocation providedStorageLocation)
specifier|public
specifier|static
name|byte
index|[]
name|toProtoBufBytes
parameter_list|(
annotation|@
name|Nonnull
name|ProvidedStorageLocation
name|providedStorageLocation
parameter_list|)
throws|throws
name|IOException
block|{
name|ProvidedStorageLocationProto
name|providedStorageLocationProto
init|=
name|PBHelperClient
operator|.
name|convert
argument_list|(
name|providedStorageLocation
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|providedStorageLocationOutputStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|providedStorageLocationProto
operator|.
name|writeTo
argument_list|(
name|providedStorageLocationOutputStream
argument_list|)
expr_stmt|;
return|return
name|providedStorageLocationOutputStream
operator|.
name|toByteArray
argument_list|()
return|;
block|}
DECL|method|toProtoBufBytes (@onnull Block block)
specifier|public
specifier|static
name|byte
index|[]
name|toProtoBufBytes
parameter_list|(
annotation|@
name|Nonnull
name|Block
name|block
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockProto
name|blockProto
init|=
name|PBHelperClient
operator|.
name|convert
argument_list|(
name|block
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|blockOutputStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|blockProto
operator|.
name|writeTo
argument_list|(
name|blockOutputStream
argument_list|)
expr_stmt|;
return|return
name|blockOutputStream
operator|.
name|toByteArray
argument_list|()
return|;
block|}
comment|/**    * Transfer this aliasmap for bootstrapping standby Namenodes. The map is    * transferred as a tar.gz archive. This archive needs to be extracted on the    * standby Namenode.    *    * @param response http response.    * @param conf configuration to use.    * @param aliasMap aliasmap to transfer.    * @throws IOException    */
DECL|method|transferForBootstrap (HttpServletResponse response, Configuration conf, InMemoryAliasMap aliasMap)
specifier|public
specifier|static
name|void
name|transferForBootstrap
parameter_list|(
name|HttpServletResponse
name|response
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|InMemoryAliasMap
name|aliasMap
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|aliasMapSnapshot
init|=
literal|null
decl_stmt|;
name|File
name|compressedAliasMap
init|=
literal|null
decl_stmt|;
try|try
block|{
name|aliasMapSnapshot
operator|=
name|createSnapshot
argument_list|(
name|aliasMap
argument_list|)
expr_stmt|;
comment|// compress the snapshot that is associated with the
comment|// block pool id of the aliasmap.
name|compressedAliasMap
operator|=
name|getCompressedAliasMap
argument_list|(
operator|new
name|File
argument_list|(
name|aliasMapSnapshot
argument_list|,
name|aliasMap
operator|.
name|blockPoolID
argument_list|)
argument_list|)
expr_stmt|;
try|try
init|(
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|compressedAliasMap
argument_list|)
init|)
block|{
name|ImageServlet
operator|.
name|setVerificationHeadersForGet
argument_list|(
name|response
argument_list|,
name|compressedAliasMap
argument_list|)
expr_stmt|;
name|ImageServlet
operator|.
name|setFileNameHeaders
argument_list|(
name|response
argument_list|,
name|compressedAliasMap
argument_list|)
expr_stmt|;
comment|// send file
name|DataTransferThrottler
name|throttler
init|=
name|ImageServlet
operator|.
name|getThrottlerForBootstrapStandby
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|TransferFsImage
operator|.
name|copyFileToStream
argument_list|(
name|response
operator|.
name|getOutputStream
argument_list|()
argument_list|,
name|compressedAliasMap
argument_list|,
name|fis
argument_list|,
name|throttler
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
comment|// cleanup the temporary snapshot and compressed files.
name|StringBuilder
name|errMessage
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|compressedAliasMap
operator|!=
literal|null
operator|&&
operator|!
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|compressedAliasMap
argument_list|)
condition|)
block|{
name|errMessage
operator|.
name|append
argument_list|(
literal|"Failed to fully delete compressed aliasmap "
argument_list|)
operator|.
name|append
argument_list|(
name|compressedAliasMap
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|aliasMapSnapshot
operator|!=
literal|null
operator|&&
operator|!
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|aliasMapSnapshot
argument_list|)
condition|)
block|{
name|errMessage
operator|.
name|append
argument_list|(
literal|"Failed to fully delete the aliasmap snapshot "
argument_list|)
operator|.
name|append
argument_list|(
name|aliasMapSnapshot
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|errMessage
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|errMessage
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Create a new LevelDB store which is a snapshot copy of the original    * aliasmap.    *    * @param aliasMap original aliasmap.    * @return the {@link File} where the snapshot is created.    * @throws IOException    */
DECL|method|createSnapshot (InMemoryAliasMap aliasMap)
specifier|static
name|File
name|createSnapshot
parameter_list|(
name|InMemoryAliasMap
name|aliasMap
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|originalAliasMapDir
init|=
operator|new
name|File
argument_list|(
name|aliasMap
operator|.
name|aliasMapURI
argument_list|)
decl_stmt|;
name|String
name|bpid
init|=
name|originalAliasMapDir
operator|.
name|getName
argument_list|()
decl_stmt|;
name|File
name|snapshotDir
init|=
operator|new
name|File
argument_list|(
name|originalAliasMapDir
operator|.
name|getParent
argument_list|()
argument_list|,
name|SNAPSHOT_COPY_DIR
argument_list|)
decl_stmt|;
name|File
name|newLevelDBDir
init|=
operator|new
name|File
argument_list|(
name|snapshotDir
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|newLevelDBDir
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create aliasmap snapshot directory "
operator|+
name|newLevelDBDir
argument_list|)
throw|;
block|}
comment|// get a snapshot for the original DB.
name|DB
name|originalDB
init|=
name|aliasMap
operator|.
name|levelDb
decl_stmt|;
try|try
init|(
name|Snapshot
name|snapshot
init|=
name|originalDB
operator|.
name|getSnapshot
argument_list|()
init|)
block|{
comment|// create a new DB for the snapshot and copy all K,V pairs.
name|Options
name|options
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|options
operator|.
name|createIfMissing
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
init|(
name|DB
name|snapshotDB
init|=
name|JniDBFactory
operator|.
name|factory
operator|.
name|open
argument_list|(
name|newLevelDBDir
argument_list|,
name|options
argument_list|)
init|)
block|{
try|try
init|(
name|DBIterator
name|iterator
init|=
name|originalDB
operator|.
name|iterator
argument_list|(
operator|new
name|ReadOptions
argument_list|()
operator|.
name|snapshot
argument_list|(
name|snapshot
argument_list|)
argument_list|)
init|)
block|{
name|iterator
operator|.
name|seekToFirst
argument_list|()
expr_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|snapshotDB
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|snapshotDir
return|;
block|}
comment|/**    * Compress the given aliasmap directory as tar.gz.    *    * @return a reference to the compressed aliasmap.    * @throws IOException    */
DECL|method|getCompressedAliasMap (File aliasMapDir)
specifier|private
specifier|static
name|File
name|getCompressedAliasMap
parameter_list|(
name|File
name|aliasMapDir
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|outCompressedFile
init|=
operator|new
name|File
argument_list|(
name|aliasMapDir
operator|.
name|getParent
argument_list|()
argument_list|,
name|TAR_NAME
argument_list|)
decl_stmt|;
name|BufferedOutputStream
name|bOut
init|=
literal|null
decl_stmt|;
name|GzipCompressorOutputStream
name|gzOut
init|=
literal|null
decl_stmt|;
name|TarArchiveOutputStream
name|tOut
init|=
literal|null
decl_stmt|;
try|try
block|{
name|bOut
operator|=
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|outCompressedFile
argument_list|)
argument_list|)
expr_stmt|;
name|gzOut
operator|=
operator|new
name|GzipCompressorOutputStream
argument_list|(
name|bOut
argument_list|)
expr_stmt|;
name|tOut
operator|=
operator|new
name|TarArchiveOutputStream
argument_list|(
name|gzOut
argument_list|)
expr_stmt|;
name|addFileToTarGzRecursively
argument_list|(
name|tOut
argument_list|,
name|aliasMapDir
argument_list|,
literal|""
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|tOut
operator|!=
literal|null
condition|)
block|{
name|tOut
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
literal|null
argument_list|,
name|tOut
argument_list|,
name|gzOut
argument_list|,
name|bOut
argument_list|)
expr_stmt|;
block|}
return|return
name|outCompressedFile
return|;
block|}
comment|/**    * Add all contents of the given file to the archive.    *    * @param tOut archive to use.    * @param file file to archive.    * @param prefix path prefix.    * @throws IOException    */
DECL|method|addFileToTarGzRecursively (TarArchiveOutputStream tOut, File file, String prefix, Configuration conf)
specifier|private
specifier|static
name|void
name|addFileToTarGzRecursively
parameter_list|(
name|TarArchiveOutputStream
name|tOut
parameter_list|,
name|File
name|file
parameter_list|,
name|String
name|prefix
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|entryName
init|=
name|prefix
operator|+
name|file
operator|.
name|getName
argument_list|()
decl_stmt|;
name|TarArchiveEntry
name|tarEntry
init|=
operator|new
name|TarArchiveEntry
argument_list|(
name|file
argument_list|,
name|entryName
argument_list|)
decl_stmt|;
name|tOut
operator|.
name|putArchiveEntry
argument_list|(
name|tarEntry
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Adding entry {} to alias map archive"
argument_list|,
name|entryName
argument_list|)
expr_stmt|;
if|if
condition|(
name|file
operator|.
name|isFile
argument_list|()
condition|)
block|{
try|try
init|(
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
init|)
block|{
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|tOut
argument_list|,
name|conf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|tOut
operator|.
name|closeArchiveEntry
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|tOut
operator|.
name|closeArchiveEntry
argument_list|()
expr_stmt|;
name|File
index|[]
name|children
init|=
name|file
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|File
name|child
range|:
name|children
control|)
block|{
comment|// skip the LOCK file
if|if
condition|(
operator|!
name|child
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"LOCK"
argument_list|)
condition|)
block|{
name|addFileToTarGzRecursively
argument_list|(
name|tOut
argument_list|,
name|child
argument_list|,
name|entryName
operator|+
literal|"/"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**    * Extract the aliasmap archive to complete the bootstrap process. This method    * has to be called after the aliasmap archive is transfered from the primary    * Namenode.    *    * @param aliasMap location of the aliasmap.    * @throws IOException    */
DECL|method|completeBootstrapTransfer (File aliasMap)
specifier|public
specifier|static
name|void
name|completeBootstrapTransfer
parameter_list|(
name|File
name|aliasMap
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|tarname
init|=
operator|new
name|File
argument_list|(
name|aliasMap
argument_list|,
name|TAR_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|tarname
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Aliasmap archive ("
operator|+
name|tarname
operator|+
literal|") does not exist"
argument_list|)
throw|;
block|}
try|try
block|{
name|FileUtil
operator|.
name|unTar
argument_list|(
name|tarname
argument_list|,
name|aliasMap
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// delete the archive.
if|if
condition|(
operator|!
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|tarname
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to fully delete aliasmap archive: "
operator|+
name|tarname
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * CheckedFunction is akin to {@link java.util.function.Function} but    * specifies an IOException.    * @param<T1> First argument type.    * @param<T2> Second argument type.    * @param<R> Return type.    */
annotation|@
name|FunctionalInterface
DECL|interface|CheckedFunction2
specifier|public
interface|interface
name|CheckedFunction2
parameter_list|<
name|T1
parameter_list|,
name|T2
parameter_list|,
name|R
parameter_list|>
block|{
DECL|method|apply (T1 t1, T2 t2)
name|R
name|apply
parameter_list|(
name|T1
name|t1
parameter_list|,
name|T2
name|t2
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
block|}
end_class

end_unit

