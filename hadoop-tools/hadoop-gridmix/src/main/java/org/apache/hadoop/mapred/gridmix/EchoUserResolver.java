begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.gridmix
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
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
name|net
operator|.
name|URI
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
name|security
operator|.
name|UserGroupInformation
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
comment|/**  * Echos the UGI offered.  */
end_comment

begin_class
DECL|class|EchoUserResolver
specifier|public
class|class
name|EchoUserResolver
implements|implements
name|UserResolver
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Gridmix
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|EchoUserResolver ()
specifier|public
name|EchoUserResolver
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|" Current user resolver is EchoUserResolver "
argument_list|)
expr_stmt|;
block|}
DECL|method|setTargetUsers (URI userdesc, Configuration conf)
specifier|public
specifier|synchronized
name|boolean
name|setTargetUsers
parameter_list|(
name|URI
name|userdesc
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
DECL|method|getTargetUgi ( UserGroupInformation ugi)
specifier|public
specifier|synchronized
name|UserGroupInformation
name|getTargetUgi
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|)
block|{
return|return
name|ugi
return|;
block|}
comment|/**    * {@inheritDoc}    *<br><br>    * Since {@link EchoUserResolver} simply returns the user's name passed as    * the argument, it doesn't need a target list of users.    */
DECL|method|needsTargetUsersList ()
specifier|public
name|boolean
name|needsTargetUsersList
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

