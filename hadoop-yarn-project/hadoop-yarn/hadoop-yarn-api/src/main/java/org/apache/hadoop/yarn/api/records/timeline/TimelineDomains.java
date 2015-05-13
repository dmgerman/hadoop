begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records.timeline
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
operator|.
name|timeline
package|;
end_package

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
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
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
comment|/**  * The class that hosts a list of timeline domains.  */
end_comment

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"domains"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|NONE
argument_list|)
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|TimelineDomains
specifier|public
class|class
name|TimelineDomains
block|{
DECL|field|domains
specifier|private
name|List
argument_list|<
name|TimelineDomain
argument_list|>
name|domains
init|=
operator|new
name|ArrayList
argument_list|<
name|TimelineDomain
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|TimelineDomains ()
specifier|public
name|TimelineDomains
parameter_list|()
block|{   }
comment|/**    * Get a list of domains    *     * @return a list of domains    */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"domains"
argument_list|)
DECL|method|getDomains ()
specifier|public
name|List
argument_list|<
name|TimelineDomain
argument_list|>
name|getDomains
parameter_list|()
block|{
return|return
name|domains
return|;
block|}
comment|/**    * Add a single domain into the existing domain list    *     * @param domain    *          a single domain    */
DECL|method|addDomain (TimelineDomain domain)
specifier|public
name|void
name|addDomain
parameter_list|(
name|TimelineDomain
name|domain
parameter_list|)
block|{
name|domains
operator|.
name|add
argument_list|(
name|domain
argument_list|)
expr_stmt|;
block|}
comment|/**    * All a list of domains into the existing domain list    *     * @param domains    *          a list of domains    */
DECL|method|addDomains (List<TimelineDomain> domains)
specifier|public
name|void
name|addDomains
parameter_list|(
name|List
argument_list|<
name|TimelineDomain
argument_list|>
name|domains
parameter_list|)
block|{
name|this
operator|.
name|domains
operator|.
name|addAll
argument_list|(
name|domains
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the domain list to the given list of domains    *     * @param domains    *          a list of domains    */
DECL|method|setDomains (List<TimelineDomain> domains)
specifier|public
name|void
name|setDomains
parameter_list|(
name|List
argument_list|<
name|TimelineDomain
argument_list|>
name|domains
parameter_list|)
block|{
name|this
operator|.
name|domains
operator|=
name|domains
expr_stmt|;
block|}
block|}
end_class

end_unit

