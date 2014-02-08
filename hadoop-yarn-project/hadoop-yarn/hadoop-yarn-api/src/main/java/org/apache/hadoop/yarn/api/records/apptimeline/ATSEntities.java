begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records.apptimeline
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
name|apptimeline
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
name|Unstable
import|;
end_import

begin_comment
comment|/**  * The class that hosts a list of application timeline entities.  */
end_comment

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"entities"
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
name|Unstable
DECL|class|ATSEntities
specifier|public
class|class
name|ATSEntities
block|{
DECL|field|entities
specifier|private
name|List
argument_list|<
name|ATSEntity
argument_list|>
name|entities
init|=
operator|new
name|ArrayList
argument_list|<
name|ATSEntity
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|ATSEntities ()
specifier|public
name|ATSEntities
parameter_list|()
block|{    }
comment|/**    * Get a list of entities    *     * @return a list of entities    */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"entities"
argument_list|)
DECL|method|getEntities ()
specifier|public
name|List
argument_list|<
name|ATSEntity
argument_list|>
name|getEntities
parameter_list|()
block|{
return|return
name|entities
return|;
block|}
comment|/**    * Add a single entity into the existing entity list    *     * @param entity    *          a single entity    */
DECL|method|addEntity (ATSEntity entity)
specifier|public
name|void
name|addEntity
parameter_list|(
name|ATSEntity
name|entity
parameter_list|)
block|{
name|entities
operator|.
name|add
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
comment|/**    * All a list of entities into the existing entity list    *     * @param entities    *          a list of entities    */
DECL|method|addEntities (List<ATSEntity> entities)
specifier|public
name|void
name|addEntities
parameter_list|(
name|List
argument_list|<
name|ATSEntity
argument_list|>
name|entities
parameter_list|)
block|{
name|this
operator|.
name|entities
operator|.
name|addAll
argument_list|(
name|entities
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the entity list to the given list of entities    *     * @param entities    *          a list of entities    */
DECL|method|setEntities (List<ATSEntity> entities)
specifier|public
name|void
name|setEntities
parameter_list|(
name|List
argument_list|<
name|ATSEntity
argument_list|>
name|entities
parameter_list|)
block|{
name|this
operator|.
name|entities
operator|=
name|entities
expr_stmt|;
block|}
block|}
end_class

end_unit

