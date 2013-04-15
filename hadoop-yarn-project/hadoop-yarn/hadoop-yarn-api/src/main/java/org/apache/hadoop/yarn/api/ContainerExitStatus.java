begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
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
operator|.
name|Public
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
operator|.
name|Evolving
import|;
end_import

begin_comment
comment|/**  * Container exit statuses indicating special exit circumstances.  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|ContainerExitStatus
specifier|public
class|class
name|ContainerExitStatus
block|{
DECL|field|SUCCESS
specifier|public
specifier|static
specifier|final
name|int
name|SUCCESS
init|=
literal|0
decl_stmt|;
DECL|field|INVALID
specifier|public
specifier|static
specifier|final
name|int
name|INVALID
init|=
operator|-
literal|1000
decl_stmt|;
comment|/**    * Containers killed by the framework, either due to being released by    * the application or being 'lost' due to node failures etc.    */
DECL|field|ABORTED
specifier|public
specifier|static
specifier|final
name|int
name|ABORTED
init|=
operator|-
literal|100
decl_stmt|;
comment|/**    * When threshold number of the nodemanager-local-directories or    * threshold number of the nodemanager-log-directories become bad.    */
DECL|field|DISKS_FAILED
specifier|public
specifier|static
specifier|final
name|int
name|DISKS_FAILED
init|=
operator|-
literal|101
decl_stmt|;
block|}
end_class

end_unit

