begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.domain
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|domain
package|;
end_package

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
name|hbase
operator|.
name|HColumnDescriptor
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
name|hbase
operator|.
name|HTableDescriptor
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
name|hbase
operator|.
name|TableName
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
name|hbase
operator|.
name|client
operator|.
name|Admin
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
name|hbase
operator|.
name|regionserver
operator|.
name|BloomType
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|common
operator|.
name|BaseTableRW
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
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|common
operator|.
name|TimelineHBaseSchemaConstants
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

begin_comment
comment|/**  * Create, read and write to the domain Table.  */
end_comment

begin_class
DECL|class|DomainTableRW
specifier|public
class|class
name|DomainTableRW
extends|extends
name|BaseTableRW
argument_list|<
name|DomainTable
argument_list|>
block|{
comment|/** domain prefix. */
DECL|field|PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|PREFIX
init|=
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_PREFIX
operator|+
literal|"domain"
decl_stmt|;
comment|/** config param name that specifies the domain table name. */
DECL|field|TABLE_NAME_CONF_NAME
specifier|public
specifier|static
specifier|final
name|String
name|TABLE_NAME_CONF_NAME
init|=
name|PREFIX
operator|+
literal|".table.name"
decl_stmt|;
comment|/** default value for domain table name. */
DECL|field|DEFAULT_TABLE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_TABLE_NAME
init|=
literal|"timelineservice.domain"
decl_stmt|;
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
name|DomainTableRW
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|DomainTableRW ()
specifier|public
name|DomainTableRW
parameter_list|()
block|{
name|super
argument_list|(
name|TABLE_NAME_CONF_NAME
argument_list|,
name|DEFAULT_TABLE_NAME
argument_list|)
expr_stmt|;
block|}
comment|/*    * (non-Javadoc)    *    * @see    * org.apache.hadoop.yarn.server.timelineservice.storage.BaseTableRW#    * createTable(org.apache.hadoop.hbase.client.Admin,    * org.apache.hadoop.conf.Configuration)    */
DECL|method|createTable (Admin admin, Configuration hbaseConf)
specifier|public
name|void
name|createTable
parameter_list|(
name|Admin
name|admin
parameter_list|,
name|Configuration
name|hbaseConf
parameter_list|)
throws|throws
name|IOException
block|{
name|TableName
name|table
init|=
name|getTableName
argument_list|(
name|hbaseConf
argument_list|)
decl_stmt|;
if|if
condition|(
name|admin
operator|.
name|tableExists
argument_list|(
name|table
argument_list|)
condition|)
block|{
comment|// do not disable / delete existing table
comment|// similar to the approach taken by map-reduce jobs when
comment|// output directory exists
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Table "
operator|+
name|table
operator|.
name|getNameAsString
argument_list|()
operator|+
literal|" already exists."
argument_list|)
throw|;
block|}
name|HTableDescriptor
name|domainTableDescp
init|=
operator|new
name|HTableDescriptor
argument_list|(
name|table
argument_list|)
decl_stmt|;
name|HColumnDescriptor
name|mappCF
init|=
operator|new
name|HColumnDescriptor
argument_list|(
name|DomainColumnFamily
operator|.
name|INFO
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|mappCF
operator|.
name|setBloomFilterType
argument_list|(
name|BloomType
operator|.
name|ROWCOL
argument_list|)
expr_stmt|;
name|domainTableDescp
operator|.
name|addFamily
argument_list|(
name|mappCF
argument_list|)
expr_stmt|;
name|domainTableDescp
operator|.
name|setRegionSplitPolicyClassName
argument_list|(
literal|"org.apache.hadoop.hbase.regionserver.KeyPrefixRegionSplitPolicy"
argument_list|)
expr_stmt|;
name|domainTableDescp
operator|.
name|setValue
argument_list|(
literal|"KeyPrefixRegionSplitPolicy.prefix_length"
argument_list|,
name|TimelineHBaseSchemaConstants
operator|.
name|USERNAME_SPLIT_KEY_PREFIX_LENGTH
argument_list|)
expr_stmt|;
name|admin
operator|.
name|createTable
argument_list|(
name|domainTableDescp
argument_list|,
name|TimelineHBaseSchemaConstants
operator|.
name|getUsernameSplits
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Status of table creation for "
operator|+
name|table
operator|.
name|getNameAsString
argument_list|()
operator|+
literal|"="
operator|+
name|admin
operator|.
name|tableExists
argument_list|(
name|table
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

