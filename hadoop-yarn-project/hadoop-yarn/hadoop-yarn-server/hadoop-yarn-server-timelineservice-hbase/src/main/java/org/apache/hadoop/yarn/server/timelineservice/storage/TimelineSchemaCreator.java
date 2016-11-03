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
name|List
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
name|application
operator|.
name|ApplicationTable
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
name|common
operator|.
name|HBaseTimelineStorageUtils
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
name|TimelineStorageUtils
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
name|flow
operator|.
name|FlowActivityTable
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
name|flow
operator|.
name|FlowRunTable
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
name|annotations
operator|.
name|VisibleForTesting
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
specifier|final
class|class
name|TimelineSchemaCreator
block|{
DECL|method|TimelineSchemaCreator ()
specifier|private
name|TimelineSchemaCreator
parameter_list|()
block|{   }
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
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TimelineSchemaCreator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SKIP_EXISTING_TABLE_OPTION_SHORT
specifier|private
specifier|static
specifier|final
name|String
name|SKIP_EXISTING_TABLE_OPTION_SHORT
init|=
literal|"s"
decl_stmt|;
DECL|field|APP_METRICS_TTL_OPTION_SHORT
specifier|private
specifier|static
specifier|final
name|String
name|APP_METRICS_TTL_OPTION_SHORT
init|=
literal|"ma"
decl_stmt|;
DECL|field|APP_TABLE_NAME_SHORT
specifier|private
specifier|static
specifier|final
name|String
name|APP_TABLE_NAME_SHORT
init|=
literal|"a"
decl_stmt|;
DECL|field|APP_TO_FLOW_TABLE_NAME_SHORT
specifier|private
specifier|static
specifier|final
name|String
name|APP_TO_FLOW_TABLE_NAME_SHORT
init|=
literal|"a2f"
decl_stmt|;
DECL|field|ENTITY_METRICS_TTL_OPTION_SHORT
specifier|private
specifier|static
specifier|final
name|String
name|ENTITY_METRICS_TTL_OPTION_SHORT
init|=
literal|"me"
decl_stmt|;
DECL|field|ENTITY_TABLE_NAME_SHORT
specifier|private
specifier|static
specifier|final
name|String
name|ENTITY_TABLE_NAME_SHORT
init|=
literal|"e"
decl_stmt|;
DECL|field|HELP_SHORT
specifier|private
specifier|static
specifier|final
name|String
name|HELP_SHORT
init|=
literal|"h"
decl_stmt|;
DECL|field|CREATE_TABLES_SHORT
specifier|private
specifier|static
specifier|final
name|String
name|CREATE_TABLES_SHORT
init|=
literal|"c"
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
name|HBaseTimelineStorageUtils
operator|.
name|getTimelineServiceHBaseConf
argument_list|(
literal|null
argument_list|)
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
if|if
condition|(
name|commandLine
operator|.
name|hasOption
argument_list|(
name|HELP_SHORT
argument_list|)
condition|)
block|{
comment|// -help option has the highest precedence
name|printUsage
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|commandLine
operator|.
name|hasOption
argument_list|(
name|CREATE_TABLES_SHORT
argument_list|)
condition|)
block|{
comment|// Grab the entityTableName argument
name|String
name|entityTableName
init|=
name|commandLine
operator|.
name|getOptionValue
argument_list|(
name|ENTITY_TABLE_NAME_SHORT
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
comment|// Grab the entity metrics TTL
name|String
name|entityTableMetricsTTL
init|=
name|commandLine
operator|.
name|getOptionValue
argument_list|(
name|ENTITY_METRICS_TTL_OPTION_SHORT
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isNotBlank
argument_list|(
name|entityTableMetricsTTL
argument_list|)
condition|)
block|{
name|int
name|entityMetricsTTL
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|entityTableMetricsTTL
argument_list|)
decl_stmt|;
operator|new
name|EntityTable
argument_list|()
operator|.
name|setMetricsTTL
argument_list|(
name|entityMetricsTTL
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
name|APP_TO_FLOW_TABLE_NAME_SHORT
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
comment|// Grab the applicationTableName argument
name|String
name|applicationTableName
init|=
name|commandLine
operator|.
name|getOptionValue
argument_list|(
name|APP_TABLE_NAME_SHORT
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isNotBlank
argument_list|(
name|applicationTableName
argument_list|)
condition|)
block|{
name|hbaseConf
operator|.
name|set
argument_list|(
name|ApplicationTable
operator|.
name|TABLE_NAME_CONF_NAME
argument_list|,
name|applicationTableName
argument_list|)
expr_stmt|;
block|}
comment|// Grab the application metrics TTL
name|String
name|applicationTableMetricsTTL
init|=
name|commandLine
operator|.
name|getOptionValue
argument_list|(
name|APP_METRICS_TTL_OPTION_SHORT
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isNotBlank
argument_list|(
name|applicationTableMetricsTTL
argument_list|)
condition|)
block|{
name|int
name|appMetricsTTL
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|applicationTableMetricsTTL
argument_list|)
decl_stmt|;
operator|new
name|ApplicationTable
argument_list|()
operator|.
name|setMetricsTTL
argument_list|(
name|appMetricsTTL
argument_list|,
name|hbaseConf
argument_list|)
expr_stmt|;
block|}
comment|// create all table schemas in hbase
specifier|final
name|boolean
name|skipExisting
init|=
name|commandLine
operator|.
name|hasOption
argument_list|(
name|SKIP_EXISTING_TABLE_OPTION_SHORT
argument_list|)
decl_stmt|;
name|createAllSchemas
argument_list|(
name|hbaseConf
argument_list|,
name|skipExisting
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// print usage information if -create is not specified
name|printUsage
argument_list|()
expr_stmt|;
block|}
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
name|HELP_SHORT
argument_list|,
literal|"help"
argument_list|,
literal|false
argument_list|,
literal|"print help information"
argument_list|)
decl_stmt|;
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
name|CREATE_TABLES_SHORT
argument_list|,
literal|"create"
argument_list|,
literal|false
argument_list|,
literal|"a mandatory option to create hbase tables"
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
name|ENTITY_TABLE_NAME_SHORT
argument_list|,
literal|"entityTableName"
argument_list|,
literal|true
argument_list|,
literal|"entity table name"
argument_list|)
expr_stmt|;
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
name|ENTITY_METRICS_TTL_OPTION_SHORT
argument_list|,
literal|"entityMetricsTTL"
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
literal|"entityMetricsTTL"
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
name|APP_TO_FLOW_TABLE_NAME_SHORT
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
name|o
operator|=
operator|new
name|Option
argument_list|(
name|APP_TABLE_NAME_SHORT
argument_list|,
literal|"applicationTableName"
argument_list|,
literal|true
argument_list|,
literal|"application table name"
argument_list|)
expr_stmt|;
name|o
operator|.
name|setArgName
argument_list|(
literal|"applicationTableName"
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
name|APP_METRICS_TTL_OPTION_SHORT
argument_list|,
literal|"applicationMetricsTTL"
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
literal|"applicationMetricsTTL"
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
comment|// Options without an argument
comment|// No need to set arg name since we do not need an argument here
name|o
operator|=
operator|new
name|Option
argument_list|(
name|SKIP_EXISTING_TABLE_OPTION_SHORT
argument_list|,
literal|"skipExistingTable"
argument_list|,
literal|false
argument_list|,
literal|"skip existing Hbase tables and continue to create new tables"
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
DECL|method|printUsage ()
specifier|private
specifier|static
name|void
name|printUsage
parameter_list|()
block|{
name|StringBuilder
name|usage
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Command Usage: \n"
argument_list|)
decl_stmt|;
name|usage
operator|.
name|append
argument_list|(
literal|"TimelineSchemaCreator [-help] Display help info"
operator|+
literal|" for all commands. Or\n"
argument_list|)
expr_stmt|;
name|usage
operator|.
name|append
argument_list|(
literal|"TimelineSchemaCreator -create [OPTIONAL_OPTIONS]"
operator|+
literal|" Create hbase tables.\n\n"
argument_list|)
expr_stmt|;
name|usage
operator|.
name|append
argument_list|(
literal|"The Optional options for creating tables include: \n"
argument_list|)
expr_stmt|;
name|usage
operator|.
name|append
argument_list|(
literal|"[-entityTableName<Entity Table Name>] "
operator|+
literal|"The name of the Entity table\n"
argument_list|)
expr_stmt|;
name|usage
operator|.
name|append
argument_list|(
literal|"[-entityMetricsTTL<Entity Table Metrics TTL>]"
operator|+
literal|" TTL for metrics in the Entity table\n"
argument_list|)
expr_stmt|;
name|usage
operator|.
name|append
argument_list|(
literal|"[-appToflowTableName<AppToflow Table Name>]"
operator|+
literal|" The name of the AppToFlow table\n"
argument_list|)
expr_stmt|;
name|usage
operator|.
name|append
argument_list|(
literal|"[-applicationTableName<Application Table Name>]"
operator|+
literal|" The name of the Application table\n"
argument_list|)
expr_stmt|;
name|usage
operator|.
name|append
argument_list|(
literal|"[-applicationMetricsTTL<Application Table Metrics TTL>]"
operator|+
literal|" TTL for metrics in the Application table\n"
argument_list|)
expr_stmt|;
name|usage
operator|.
name|append
argument_list|(
literal|"[-skipExistingTable] Whether to skip existing"
operator|+
literal|" hbase tables\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|usage
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create all table schemas and log success or exception if failed.    * @param hbaseConf the hbase configuration to create tables with    * @param skipExisting whether to skip existing hbase tables    */
DECL|method|createAllSchemas (Configuration hbaseConf, boolean skipExisting)
specifier|private
specifier|static
name|void
name|createAllSchemas
parameter_list|(
name|Configuration
name|hbaseConf
parameter_list|,
name|boolean
name|skipExisting
parameter_list|)
block|{
name|List
argument_list|<
name|Exception
argument_list|>
name|exceptions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|skipExisting
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Will skip existing tables and continue on htable creation "
operator|+
literal|"exceptions!"
argument_list|)
expr_stmt|;
block|}
name|createAllTables
argument_list|(
name|hbaseConf
argument_list|,
name|skipExisting
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully created HBase schema. "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in creating hbase tables: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|exceptions
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Schema creation finished with the following exceptions"
argument_list|)
expr_stmt|;
for|for
control|(
name|Exception
name|e
range|:
name|exceptions
control|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Schema creation finished successfully"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|createAllTables (Configuration hbaseConf, boolean skipExisting)
specifier|public
specifier|static
name|void
name|createAllTables
parameter_list|(
name|Configuration
name|hbaseConf
parameter_list|,
name|boolean
name|skipExisting
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
try|try
block|{
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
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|skipExisting
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Skip and continue on: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
try|try
block|{
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|skipExisting
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Skip and continue on: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
try|try
block|{
operator|new
name|ApplicationTable
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|skipExisting
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Skip and continue on: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
try|try
block|{
operator|new
name|FlowRunTable
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|skipExisting
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Skip and continue on: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
try|try
block|{
operator|new
name|FlowActivityTable
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|skipExisting
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Skip and continue on: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
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

