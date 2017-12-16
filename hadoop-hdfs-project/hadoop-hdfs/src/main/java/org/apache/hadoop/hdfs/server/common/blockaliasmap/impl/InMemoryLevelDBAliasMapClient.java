begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.common.blockaliasmap.impl
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
name|common
operator|.
name|blockaliasmap
operator|.
name|impl
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
name|protocolPB
operator|.
name|InMemoryAliasMapProtocolClientSideTranslatorPB
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
name|aliasmap
operator|.
name|InMemoryAliasMap
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
name|blockaliasmap
operator|.
name|BlockAliasMap
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
name|security
operator|.
name|UserGroupInformation
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
name|Iterator
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
name|NoSuchElementException
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
comment|/**  * InMemoryLevelDBAliasMapClient is the client for the InMemoryAliasMapServer.  * This is used by the Datanode and fs2img to store and retrieve FileRegions  * based on the given Block.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|InMemoryLevelDBAliasMapClient
specifier|public
class|class
name|InMemoryLevelDBAliasMapClient
extends|extends
name|BlockAliasMap
argument_list|<
name|FileRegion
argument_list|>
implements|implements
name|Configurable
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|aliasMap
specifier|private
name|InMemoryAliasMapProtocolClientSideTranslatorPB
name|aliasMap
decl_stmt|;
DECL|field|blockPoolID
specifier|private
name|String
name|blockPoolID
decl_stmt|;
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
name|aliasMap
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|class|LevelDbReader
class|class
name|LevelDbReader
extends|extends
name|BlockAliasMap
operator|.
name|Reader
argument_list|<
name|FileRegion
argument_list|>
block|{
annotation|@
name|Override
DECL|method|resolve (Block block)
specifier|public
name|Optional
argument_list|<
name|FileRegion
argument_list|>
name|resolve
parameter_list|(
name|Block
name|block
parameter_list|)
throws|throws
name|IOException
block|{
name|Optional
argument_list|<
name|ProvidedStorageLocation
argument_list|>
name|read
init|=
name|aliasMap
operator|.
name|read
argument_list|(
name|block
argument_list|)
decl_stmt|;
return|return
name|read
operator|.
name|map
argument_list|(
name|psl
lambda|->
operator|new
name|FileRegion
argument_list|(
name|block
argument_list|,
name|psl
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{     }
DECL|class|LevelDbIterator
specifier|private
class|class
name|LevelDbIterator
extends|extends
name|BlockAliasMap
argument_list|<
name|FileRegion
argument_list|>
operator|.
name|ImmutableIterator
block|{
DECL|field|iterator
specifier|private
name|Iterator
argument_list|<
name|FileRegion
argument_list|>
name|iterator
decl_stmt|;
DECL|field|nextMarker
specifier|private
name|Optional
argument_list|<
name|Block
argument_list|>
name|nextMarker
decl_stmt|;
DECL|method|LevelDbIterator ()
name|LevelDbIterator
parameter_list|()
block|{
name|batch
argument_list|(
name|Optional
operator|.
name|empty
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|batch (Optional<Block> newNextMarker)
specifier|private
name|void
name|batch
parameter_list|(
name|Optional
argument_list|<
name|Block
argument_list|>
name|newNextMarker
parameter_list|)
block|{
try|try
block|{
name|InMemoryAliasMap
operator|.
name|IterationResult
name|iterationResult
init|=
name|aliasMap
operator|.
name|list
argument_list|(
name|newNextMarker
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FileRegion
argument_list|>
name|fileRegions
init|=
name|iterationResult
operator|.
name|getFileRegions
argument_list|()
decl_stmt|;
name|this
operator|.
name|iterator
operator|=
name|fileRegions
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|this
operator|.
name|nextMarker
operator|=
name|iterationResult
operator|.
name|getNextBlock
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
operator|||
name|nextMarker
operator|.
name|isPresent
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|FileRegion
name|next
parameter_list|()
block|{
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|iterator
operator|.
name|next
argument_list|()
return|;
block|}
else|else
block|{
if|if
condition|(
name|nextMarker
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|batch
argument_list|(
name|nextMarker
argument_list|)
expr_stmt|;
return|return
name|next
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|Iterator
argument_list|<
name|FileRegion
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|LevelDbIterator
argument_list|()
return|;
block|}
block|}
DECL|class|LevelDbWriter
class|class
name|LevelDbWriter
extends|extends
name|BlockAliasMap
operator|.
name|Writer
argument_list|<
name|FileRegion
argument_list|>
block|{
annotation|@
name|Override
DECL|method|store (FileRegion fileRegion)
specifier|public
name|void
name|store
parameter_list|(
name|FileRegion
name|fileRegion
parameter_list|)
throws|throws
name|IOException
block|{
name|aliasMap
operator|.
name|write
argument_list|(
name|fileRegion
operator|.
name|getBlock
argument_list|()
argument_list|,
name|fileRegion
operator|.
name|getProvidedStorageLocation
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{     }
block|}
DECL|method|InMemoryLevelDBAliasMapClient ()
name|InMemoryLevelDBAliasMapClient
parameter_list|()
block|{
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Unable to start "
operator|+
literal|"InMemoryLevelDBAliasMapClient as security is enabled"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getReader (Reader.Options opts, String blockPoolID)
specifier|public
name|Reader
argument_list|<
name|FileRegion
argument_list|>
name|getReader
parameter_list|(
name|Reader
operator|.
name|Options
name|opts
parameter_list|,
name|String
name|blockPoolID
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|blockPoolID
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|blockPoolID
operator|=
name|aliasMap
operator|.
name|getBlockPoolId
argument_list|()
expr_stmt|;
block|}
comment|// if a block pool id has been supplied, and doesn't match the associated
comment|// block pool id, return null.
if|if
condition|(
name|blockPoolID
operator|!=
literal|null
operator|&&
name|this
operator|.
name|blockPoolID
operator|!=
literal|null
operator|&&
operator|!
name|this
operator|.
name|blockPoolID
operator|.
name|equals
argument_list|(
name|blockPoolID
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|LevelDbReader
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getWriter (Writer.Options opts, String blockPoolID)
specifier|public
name|Writer
argument_list|<
name|FileRegion
argument_list|>
name|getWriter
parameter_list|(
name|Writer
operator|.
name|Options
name|opts
parameter_list|,
name|String
name|blockPoolID
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|blockPoolID
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|blockPoolID
operator|=
name|aliasMap
operator|.
name|getBlockPoolId
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|blockPoolID
operator|!=
literal|null
operator|&&
operator|!
name|this
operator|.
name|blockPoolID
operator|.
name|equals
argument_list|(
name|blockPoolID
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|LevelDbWriter
argument_list|()
return|;
block|}
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
name|this
operator|.
name|aliasMap
operator|=
operator|new
name|InMemoryAliasMapProtocolClientSideTranslatorPB
argument_list|(
name|conf
argument_list|)
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
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|refresh ()
specifier|public
name|void
name|refresh
parameter_list|()
throws|throws
name|IOException
block|{   }
block|}
end_class

end_unit

