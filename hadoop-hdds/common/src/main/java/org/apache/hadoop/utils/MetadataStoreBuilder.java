begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|rocksdb
operator|.
name|BlockBasedTableConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|rocksdb
operator|.
name|Statistics
import|;
end_import

begin_import
import|import
name|org
operator|.
name|rocksdb
operator|.
name|StatsLevel
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
name|OZONE_METADATA_STORE_IMPL_LEVELDB
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
name|OZONE_METADATA_STORE_IMPL_ROCKSDB
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
name|OZONE_METADATA_STORE_ROCKSDB_STATISTICS
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
name|OZONE_METADATA_STORE_ROCKSDB_STATISTICS_DEFAULT
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
name|OZONE_METADATA_STORE_ROCKSDB_STATISTICS_OFF
import|;
end_import

begin_comment
comment|/**  * Builder for metadata store.  */
end_comment

begin_class
DECL|class|MetadataStoreBuilder
specifier|public
class|class
name|MetadataStoreBuilder
block|{
annotation|@
name|VisibleForTesting
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MetadataStoreBuilder
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|dbFile
specifier|private
name|File
name|dbFile
decl_stmt|;
DECL|field|cacheSize
specifier|private
name|long
name|cacheSize
decl_stmt|;
DECL|field|createIfMissing
specifier|private
name|boolean
name|createIfMissing
init|=
literal|true
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|dbType
specifier|private
name|String
name|dbType
decl_stmt|;
DECL|method|newBuilder ()
specifier|public
specifier|static
name|MetadataStoreBuilder
name|newBuilder
parameter_list|()
block|{
return|return
operator|new
name|MetadataStoreBuilder
argument_list|()
return|;
block|}
DECL|method|setDbFile (File dbPath)
specifier|public
name|MetadataStoreBuilder
name|setDbFile
parameter_list|(
name|File
name|dbPath
parameter_list|)
block|{
name|this
operator|.
name|dbFile
operator|=
name|dbPath
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setCacheSize (long cache)
specifier|public
name|MetadataStoreBuilder
name|setCacheSize
parameter_list|(
name|long
name|cache
parameter_list|)
block|{
name|this
operator|.
name|cacheSize
operator|=
name|cache
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setCreateIfMissing (boolean doCreate)
specifier|public
name|MetadataStoreBuilder
name|setCreateIfMissing
parameter_list|(
name|boolean
name|doCreate
parameter_list|)
block|{
name|this
operator|.
name|createIfMissing
operator|=
name|doCreate
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setConf (Configuration configuration)
specifier|public
name|MetadataStoreBuilder
name|setConf
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|configuration
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Set the container DB Type.    * @param type    * @return MetadataStoreBuilder    */
DECL|method|setDBType (String type)
specifier|public
name|MetadataStoreBuilder
name|setDBType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|dbType
operator|=
name|type
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|MetadataStore
name|build
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|dbFile
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Failed to build metadata store, "
operator|+
literal|"dbFile is required but not found"
argument_list|)
throw|;
block|}
comment|// Build db store based on configuration
name|MetadataStore
name|store
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|dbType
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"dbType is null, using "
argument_list|)
expr_stmt|;
name|dbType
operator|=
name|conf
operator|==
literal|null
condition|?
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_STORE_IMPL_DEFAULT
else|:
name|conf
operator|.
name|getTrimmed
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_STORE_IMPL
argument_list|,
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_STORE_IMPL_DEFAULT
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"dbType is null, using dbType {} from ozone configuration"
argument_list|,
name|dbType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using dbType {} for metastore"
argument_list|,
name|dbType
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|OZONE_METADATA_STORE_IMPL_LEVELDB
operator|.
name|equals
argument_list|(
name|dbType
argument_list|)
condition|)
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
name|createIfMissing
argument_list|)
expr_stmt|;
if|if
condition|(
name|cacheSize
operator|>
literal|0
condition|)
block|{
name|options
operator|.
name|cacheSize
argument_list|(
name|cacheSize
argument_list|)
expr_stmt|;
block|}
name|store
operator|=
operator|new
name|LevelDBStore
argument_list|(
name|dbFile
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|OZONE_METADATA_STORE_IMPL_ROCKSDB
operator|.
name|equals
argument_list|(
name|dbType
argument_list|)
condition|)
block|{
name|org
operator|.
name|rocksdb
operator|.
name|Options
name|opts
init|=
operator|new
name|org
operator|.
name|rocksdb
operator|.
name|Options
argument_list|()
decl_stmt|;
name|opts
operator|.
name|setCreateIfMissing
argument_list|(
name|createIfMissing
argument_list|)
expr_stmt|;
if|if
condition|(
name|cacheSize
operator|>
literal|0
condition|)
block|{
name|BlockBasedTableConfig
name|tableConfig
init|=
operator|new
name|BlockBasedTableConfig
argument_list|()
decl_stmt|;
name|tableConfig
operator|.
name|setBlockCacheSize
argument_list|(
name|cacheSize
argument_list|)
expr_stmt|;
name|opts
operator|.
name|setTableFormatConfig
argument_list|(
name|tableConfig
argument_list|)
expr_stmt|;
block|}
name|String
name|rocksDbStat
init|=
name|conf
operator|==
literal|null
condition|?
name|OZONE_METADATA_STORE_ROCKSDB_STATISTICS_DEFAULT
else|:
name|conf
operator|.
name|getTrimmed
argument_list|(
name|OZONE_METADATA_STORE_ROCKSDB_STATISTICS
argument_list|,
name|OZONE_METADATA_STORE_ROCKSDB_STATISTICS_DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|rocksDbStat
operator|.
name|equals
argument_list|(
name|OZONE_METADATA_STORE_ROCKSDB_STATISTICS_OFF
argument_list|)
condition|)
block|{
name|Statistics
name|statistics
init|=
operator|new
name|Statistics
argument_list|()
decl_stmt|;
name|statistics
operator|.
name|setStatsLevel
argument_list|(
name|StatsLevel
operator|.
name|valueOf
argument_list|(
name|rocksDbStat
argument_list|)
argument_list|)
expr_stmt|;
name|opts
operator|=
name|opts
operator|.
name|setStatistics
argument_list|(
name|statistics
argument_list|)
expr_stmt|;
block|}
name|store
operator|=
operator|new
name|RocksDBStore
argument_list|(
name|dbFile
argument_list|,
name|opts
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid argument for "
operator|+
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_STORE_IMPL
operator|+
literal|". Expecting "
operator|+
name|OZONE_METADATA_STORE_IMPL_LEVELDB
operator|+
literal|" or "
operator|+
name|OZONE_METADATA_STORE_IMPL_ROCKSDB
operator|+
literal|", but met "
operator|+
name|dbType
argument_list|)
throw|;
block|}
return|return
name|store
return|;
block|}
block|}
end_class

end_unit

