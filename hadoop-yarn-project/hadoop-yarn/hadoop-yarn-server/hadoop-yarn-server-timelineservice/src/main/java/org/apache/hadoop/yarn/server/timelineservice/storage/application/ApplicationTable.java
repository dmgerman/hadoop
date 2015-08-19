begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.application
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
name|application
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
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|BaseTable
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

begin_comment
comment|/**  * The application table as column families info, config and metrics. Info  * stores information about a YARN application entity, config stores  * configuration data of a YARN application, metrics stores the metrics of a  * YARN application. This table is entirely analogous to the entity table but  * created for better performance.  *  * Example application table record:  *  *<pre>  * |-------------------------------------------------------------------------|  * |  Row       | Column Family                | Column Family| Column Family|  * |  key       | info                         | metrics      | config       |  * |-------------------------------------------------------------------------|  * | clusterId! | id:appId                     | metricId1:   | configKey1:  |  * | userName!  |                              | metricValue1 | configValue1 |  * | flowId!    | created_time:                | @timestamp1  |              |  * | flowRunId! | 1392993084018                |              | configKey2:  |  * | AppId      |                              | metriciD1:   | configValue2 |  * |            | modified_time:               | metricValue2 |              |  * |            | 1392995081012                | @timestamp2  |              |  * |            |                              |              |              |  * |            | i!infoKey:                   | metricId2:   |              |  * |            | infoValue                    | metricValue1 |              |  * |            |                              | @timestamp2  |              |  * |            | r!relatesToKey:              |              |              |  * |            | id3=id4=id5                  |              |              |  * |            |                              |              |              |  * |            | s!isRelatedToKey:            |              |              |  * |            | id7=id9=id6                  |              |              |  * |            |                              |              |              |  * |            | e!eventId=timestamp=infoKey: |              |              |  * |            | eventInfoValue               |              |              |  * |            |                              |              |              |  * |            | flowVersion:                 |              |              |  * |            | versionValue                 |              |              |  * |-------------------------------------------------------------------------|  *</pre>  */
end_comment

begin_class
DECL|class|ApplicationTable
specifier|public
class|class
name|ApplicationTable
extends|extends
name|BaseTable
argument_list|<
name|ApplicationTable
argument_list|>
block|{
comment|/** application prefix */
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
literal|".application"
decl_stmt|;
comment|/** config param name that specifies the application table name */
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
comment|/**    * config param name that specifies the TTL for metrics column family in    * application table    */
DECL|field|METRICS_TTL_CONF_NAME
specifier|private
specifier|static
specifier|final
name|String
name|METRICS_TTL_CONF_NAME
init|=
name|PREFIX
operator|+
literal|".table.metrics.ttl"
decl_stmt|;
comment|/** default value for application table name */
DECL|field|DEFAULT_TABLE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_TABLE_NAME
init|=
literal|"timelineservice.application"
decl_stmt|;
comment|/** default TTL is 30 days for metrics timeseries */
DECL|field|DEFAULT_METRICS_TTL
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_METRICS_TTL
init|=
literal|2592000
decl_stmt|;
comment|/** default max number of versions */
DECL|field|DEFAULT_METRICS_MAX_VERSIONS
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_METRICS_MAX_VERSIONS
init|=
literal|1000
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ApplicationTable
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|ApplicationTable ()
specifier|public
name|ApplicationTable
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
comment|/*    * (non-Javadoc)    *    * @see    * org.apache.hadoop.yarn.server.timelineservice.storage.BaseTable#createTable    * (org.apache.hadoop.hbase.client.Admin,    * org.apache.hadoop.conf.Configuration)    */
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
name|applicationTableDescp
init|=
operator|new
name|HTableDescriptor
argument_list|(
name|table
argument_list|)
decl_stmt|;
name|HColumnDescriptor
name|infoCF
init|=
operator|new
name|HColumnDescriptor
argument_list|(
name|ApplicationColumnFamily
operator|.
name|INFO
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|infoCF
operator|.
name|setBloomFilterType
argument_list|(
name|BloomType
operator|.
name|ROWCOL
argument_list|)
expr_stmt|;
name|applicationTableDescp
operator|.
name|addFamily
argument_list|(
name|infoCF
argument_list|)
expr_stmt|;
name|HColumnDescriptor
name|configCF
init|=
operator|new
name|HColumnDescriptor
argument_list|(
name|ApplicationColumnFamily
operator|.
name|CONFIGS
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|configCF
operator|.
name|setBloomFilterType
argument_list|(
name|BloomType
operator|.
name|ROWCOL
argument_list|)
expr_stmt|;
name|configCF
operator|.
name|setBlockCacheEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|applicationTableDescp
operator|.
name|addFamily
argument_list|(
name|configCF
argument_list|)
expr_stmt|;
name|HColumnDescriptor
name|metricsCF
init|=
operator|new
name|HColumnDescriptor
argument_list|(
name|ApplicationColumnFamily
operator|.
name|METRICS
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|applicationTableDescp
operator|.
name|addFamily
argument_list|(
name|metricsCF
argument_list|)
expr_stmt|;
name|metricsCF
operator|.
name|setBlockCacheEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// always keep 1 version (the latest)
name|metricsCF
operator|.
name|setMinVersions
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|metricsCF
operator|.
name|setMaxVersions
argument_list|(
name|DEFAULT_METRICS_MAX_VERSIONS
argument_list|)
expr_stmt|;
name|metricsCF
operator|.
name|setTimeToLive
argument_list|(
name|hbaseConf
operator|.
name|getInt
argument_list|(
name|METRICS_TTL_CONF_NAME
argument_list|,
name|DEFAULT_METRICS_TTL
argument_list|)
argument_list|)
expr_stmt|;
name|applicationTableDescp
operator|.
name|setRegionSplitPolicyClassName
argument_list|(
literal|"org.apache.hadoop.hbase.regionserver.KeyPrefixRegionSplitPolicy"
argument_list|)
expr_stmt|;
name|applicationTableDescp
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
name|applicationTableDescp
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
comment|/**    * @param metricsTTL time to live parameter for the metrics in this table.    * @param hbaseConf configuration in which to set the metrics TTL config    *          variable.    */
DECL|method|setMetricsTTL (int metricsTTL, Configuration hbaseConf)
specifier|public
name|void
name|setMetricsTTL
parameter_list|(
name|int
name|metricsTTL
parameter_list|,
name|Configuration
name|hbaseConf
parameter_list|)
block|{
name|hbaseConf
operator|.
name|setInt
argument_list|(
name|METRICS_TTL_CONF_NAME
argument_list|,
name|metricsTTL
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

