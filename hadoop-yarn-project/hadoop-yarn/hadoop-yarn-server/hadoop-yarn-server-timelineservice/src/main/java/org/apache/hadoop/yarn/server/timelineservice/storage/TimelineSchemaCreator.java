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
name|conf
operator|.
name|Configured
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
name|ReflectionUtils
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
name|Tool
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
name|ToolRunner
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
name|exceptions
operator|.
name|YarnRuntimeException
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
comment|/**  * This creates the timeline schema for storing application timeline  * information. Each backend has to implement the {@link SchemaCreator} for  * creating the schema in its backend and should be configured in yarn-site.xml.  */
end_comment

begin_class
DECL|class|TimelineSchemaCreator
specifier|public
class|class
name|TimelineSchemaCreator
extends|extends
name|Configured
implements|implements
name|Tool
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
block|{
try|try
block|{
name|int
name|status
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|,
operator|new
name|TimelineSchemaCreator
argument_list|()
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|status
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
literal|"Error while creating Timeline Schema : "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
return|return
name|createTimelineSchema
argument_list|(
name|args
argument_list|,
name|conf
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|createTimelineSchema (String[] args, Configuration conf)
name|int
name|createTimelineSchema
parameter_list|(
name|String
index|[]
name|args
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|schemaCreatorClassName
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_SCHEMA_CREATOR_CLASS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_SCHEMA_CREATOR_CLASS
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Using {} for creating Timeline Service Schema "
argument_list|,
name|schemaCreatorClassName
argument_list|)
expr_stmt|;
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|schemaCreatorClass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|schemaCreatorClassName
argument_list|)
decl_stmt|;
if|if
condition|(
name|SchemaCreator
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|schemaCreatorClass
argument_list|)
condition|)
block|{
name|SchemaCreator
name|schemaCreator
init|=
operator|(
name|SchemaCreator
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|schemaCreatorClass
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|schemaCreator
operator|.
name|createTimelineSchema
argument_list|(
name|args
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Class: "
operator|+
name|schemaCreatorClassName
operator|+
literal|" not instance of "
operator|+
name|SchemaCreator
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Could not instantiate TimelineReader: "
operator|+
name|schemaCreatorClassName
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

