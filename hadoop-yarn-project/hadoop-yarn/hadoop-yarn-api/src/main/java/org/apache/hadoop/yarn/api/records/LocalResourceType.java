begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
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
operator|.
name|records
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
name|Stable
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
name|api
operator|.
name|ContainerManagementProtocol
import|;
end_import

begin_comment
comment|/**  * {@code LocalResourceType} specifies the<em>type</em>  * of a resource localized by the {@code NodeManager}.  *<p>  * The<em>type</em> can be one of:  *<ul>  *<li>  *     {@link #FILE} - Regular file i.e. uninterpreted bytes.  *</li>  *<li>  *     {@link #ARCHIVE} - Archive, which is automatically unarchived by the  *<code>NodeManager</code>.  *</li>  *<li>  *     {@link #PATTERN} - A hybrid between {@link #ARCHIVE} and {@link #FILE}.  *</li>  *</ul>  *  * @see LocalResource  * @see ContainerLaunchContext  * @see ApplicationSubmissionContext  * @see ContainerManagementProtocol#startContainers(org.apache.hadoop.yarn.api.protocolrecords.StartContainersRequest)  */
end_comment

begin_enum
annotation|@
name|Public
annotation|@
name|Stable
DECL|enum|LocalResourceType
specifier|public
enum|enum
name|LocalResourceType
block|{
comment|/**    * Archive, which is automatically unarchived by the<code>NodeManager</code>.    */
DECL|enumConstant|ARCHIVE
name|ARCHIVE
block|,
comment|/**    * Regular file i.e. uninterpreted bytes.    */
DECL|enumConstant|FILE
name|FILE
block|,
comment|/**    * A hybrid between archive and file.  Only part of the file is unarchived,    * and the original file is left in place, but in the same directory as the    * unarchived part.  The part that is unarchived is determined by pattern    * in #{@link LocalResource}.  Currently only jars support pattern, all    * others will be treated like a #{@link LocalResourceType#ARCHIVE}.    */
DECL|enumConstant|PATTERN
name|PATTERN
block|}
end_enum

end_unit

