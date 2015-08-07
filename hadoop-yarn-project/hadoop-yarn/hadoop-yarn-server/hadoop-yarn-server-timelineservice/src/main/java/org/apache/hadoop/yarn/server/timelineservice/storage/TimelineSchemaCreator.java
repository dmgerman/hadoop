begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage
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
name|cli
operator|.
name|CommandLine
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
name|cli
operator|.
name|CommandLineParser
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
name|cli
operator|.
name|HelpFormatter
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
name|cli
operator|.
name|Option
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
name|cli
operator|.
name|Options
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
name|cli
operator|.
name|ParseException
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
name|cli
operator|.
name|PosixParser
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
name|lang
operator|.
name|StringUtils
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
name|HBaseConfiguration
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
name|client
operator|.
name|Connection
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
name|ConnectionFactory
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
name|GenericOptionsParser
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
name|apptoflow
operator|.
name|AppToFlowTable
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
name|entity
operator|.
name|EntityTable
import|;
end_import

begin_comment
comment|/**  * This creates the schema for a hbase based backend for storing application  * timeline information.  */
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
DECL|class|TimelineSchemaCreator
specifier|public
class|class
name|TimelineSchemaCreator
block|{
DECL|field|NAME
specifier|final
specifier|static
name|String
name|NAME
init|=
name|TimelineSchemaCreator
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
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
name|TimelineSchemaCreator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Configuration
name|hbaseConf
init|=
name|HBaseConfiguration
operator|.
name|create
argument_list|()
decl_stmt|;
comment|// Grab input args and allow for -Dxyz style arguments
name|String
index|[]
name|otherArgs
init|=
operator|new
name|GenericOptionsParser
argument_list|(
name|hbaseConf
argument_list|,
name|args
argument_list|)
operator|.
name|getRemainingArgs
argument_list|()
decl_stmt|;
comment|// Grab the arguments we're looking for.
name|CommandLine
name|commandLine
init|=
name|parseArgs
argument_list|(
name|otherArgs
argument_list|)
decl_stmt|;
comment|// Grab the entityTableName argument
name|String
name|entityTableName
init|=
name|commandLine
operator|.
name|getOptionValue
argument_list|(
literal|"e"
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isNotBlank
argument_list|(
name|entityTableName
argument_list|)
condition|)
block|{
name|hbaseConf
operator|.
name|set
argument_list|(
name|EntityTable
operator|.
name|TABLE_NAME_CONF_NAME
argument_list|,
name|entityTableName
argument_list|)
expr_stmt|;
block|}
name|String
name|entityTableTTLMetrics
init|=
name|commandLine
operator|.
name|getOptionValue
argument_list|(
literal|"m"
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isNotBlank
argument_list|(
name|entityTableTTLMetrics
argument_list|)
condition|)
block|{
name|int
name|metricsTTL
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|entityTableTTLMetrics
argument_list|)
decl_stmt|;
operator|new
name|EntityTable
argument_list|()
operator|.
name|setMetricsTTL
argument_list|(
name|metricsTTL
argument_list|,
name|hbaseConf
argument_list|)
expr_stmt|;
block|}
comment|// Grab the appToflowTableName argument
name|String
name|appToflowTableName
init|=
name|commandLine
operator|.
name|getOptionValue
argument_list|(
literal|"a2f"
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isNotBlank
argument_list|(
name|appToflowTableName
argument_list|)
condition|)
block|{
name|hbaseConf
operator|.
name|set
argument_list|(
name|AppToFlowTable
operator|.
name|TABLE_NAME_CONF_NAME
argument_list|,
name|appToflowTableName
argument_list|)
expr_stmt|;
block|}
name|createAllTables
argument_list|(
name|hbaseConf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Parse command-line arguments.    *    * @param args    *          command line arguments passed to program.    * @return parsed command line.    * @throws ParseException    */
DECL|method|parseArgs (String[] args)
specifier|private
specifier|static
name|CommandLine
name|parseArgs
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|ParseException
block|{
name|Options
name|options
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
comment|// Input
name|Option
name|o
init|=
operator|new
name|Option
argument_list|(
literal|"e"
argument_list|,
literal|"entityTableName"
argument_list|,
literal|true
argument_list|,
literal|"entity table name"
argument_list|)
decl_stmt|;
name|o
operator|.
name|setArgName
argument_list|(
literal|"entityTableName"
argument_list|)
expr_stmt|;
name|o
operator|.
name|setRequired
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|o
argument_list|)
expr_stmt|;
name|o
operator|=
operator|new
name|Option
argument_list|(
literal|"m"
argument_list|,
literal|"metricsTTL"
argument_list|,
literal|true
argument_list|,
literal|"TTL for metrics column family"
argument_list|)
expr_stmt|;
name|o
operator|.
name|setArgName
argument_list|(
literal|"metricsTTL"
argument_list|)
expr_stmt|;
name|o
operator|.
name|setRequired
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|o
argument_list|)
expr_stmt|;
name|o
operator|=
operator|new
name|Option
argument_list|(
literal|"a2f"
argument_list|,
literal|"appToflowTableName"
argument_list|,
literal|true
argument_list|,
literal|"app to flow table name"
argument_list|)
expr_stmt|;
name|o
operator|.
name|setArgName
argument_list|(
literal|"appToflowTableName"
argument_list|)
expr_stmt|;
name|o
operator|.
name|setRequired
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|o
argument_list|)
expr_stmt|;
name|CommandLineParser
name|parser
init|=
operator|new
name|PosixParser
argument_list|()
decl_stmt|;
name|CommandLine
name|commandLine
init|=
literal|null
decl_stmt|;
try|try
block|{
name|commandLine
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|options
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
name|LOG
operator|.
name|error
argument_list|(
literal|"ERROR: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|HelpFormatter
name|formatter
init|=
operator|new
name|HelpFormatter
argument_list|()
decl_stmt|;
name|formatter
operator|.
name|printHelp
argument_list|(
name|NAME
operator|+
literal|" "
argument_list|,
name|options
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|commandLine
return|;
block|}
DECL|method|createAllTables (Configuration hbaseConf)
specifier|private
specifier|static
name|void
name|createAllTables
parameter_list|(
name|Configuration
name|hbaseConf
parameter_list|)
throws|throws
name|IOException
block|{
name|Connection
name|conn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|conn
operator|=
name|ConnectionFactory
operator|.
name|createConnection
argument_list|(
name|hbaseConf
argument_list|)
expr_stmt|;
name|Admin
name|admin
init|=
name|conn
operator|.
name|getAdmin
argument_list|()
decl_stmt|;
if|if
condition|(
name|admin
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot create table since admin is null"
argument_list|)
throw|;
block|}
operator|new
name|EntityTable
argument_list|()
operator|.
name|createTable
argument_list|(
name|admin
argument_list|,
name|hbaseConf
argument_list|)
expr_stmt|;
operator|new
name|AppToFlowTable
argument_list|()
operator|.
name|createTable
argument_list|(
name|admin
argument_list|,
name|hbaseConf
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|conn
operator|!=
literal|null
condition|)
block|{
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

