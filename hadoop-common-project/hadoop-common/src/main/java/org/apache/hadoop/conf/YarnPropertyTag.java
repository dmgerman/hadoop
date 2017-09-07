begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
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

begin_comment
comment|/***************************************************************************  * Enum for tagging yarn properties according to there usage or application.  * YarnPropertyTag implements the  * {@link org.apache.hadoop.conf.PropertyTag} interface,  ***************************************************************************/
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|enum|YarnPropertyTag
specifier|public
enum|enum
name|YarnPropertyTag
implements|implements
name|PropertyTag
block|{
DECL|enumConstant|YARN
name|YARN
block|,
DECL|enumConstant|RESOURCEMANAGER
name|RESOURCEMANAGER
block|,
DECL|enumConstant|SECURITY
name|SECURITY
block|,
DECL|enumConstant|KERBEROS
name|KERBEROS
block|,
DECL|enumConstant|PERFORMANCE
name|PERFORMANCE
block|,
DECL|enumConstant|CLIENT
name|CLIENT
block|,
DECL|enumConstant|REQUIRED
name|REQUIRED
block|,
DECL|enumConstant|SERVER
name|SERVER
block|,
DECL|enumConstant|DEBUG
name|DEBUG
block|}
end_enum

end_unit

