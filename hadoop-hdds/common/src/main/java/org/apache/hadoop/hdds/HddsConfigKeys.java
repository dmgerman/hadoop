begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
import|;
end_import

begin_comment
comment|/**  * Config class for HDDS.  */
end_comment

begin_class
DECL|class|HddsConfigKeys
specifier|public
specifier|final
class|class
name|HddsConfigKeys
block|{
DECL|method|HddsConfigKeys ()
specifier|private
name|HddsConfigKeys
parameter_list|()
block|{   }
DECL|field|HDDS_COMMAND_STATUS_REPORT_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_COMMAND_STATUS_REPORT_INTERVAL
init|=
literal|"hdds.command.status.report.interval"
decl_stmt|;
DECL|field|HDDS_COMMAND_STATUS_REPORT_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_COMMAND_STATUS_REPORT_INTERVAL_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HEARBEAT_INTERVAL_DEFAULT
decl_stmt|;
block|}
end_class

end_unit

