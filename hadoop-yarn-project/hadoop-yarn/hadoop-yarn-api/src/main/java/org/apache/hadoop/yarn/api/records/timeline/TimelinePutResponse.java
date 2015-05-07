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

begin_comment
comment|/**  * A class that holds a list of put errors. This is the response returned when a  * list of {@link TimelineEntity} objects is added to the timeline. If there are errors  * in storing individual entity objects, they will be indicated in the list of  * errors.  */
end_comment

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"response"
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
DECL|class|TimelinePutResponse
specifier|public
class|class
name|TimelinePutResponse
block|{
DECL|field|errors
specifier|private
name|List
argument_list|<
name|TimelinePutError
argument_list|>
name|errors
init|=
operator|new
name|ArrayList
argument_list|<
name|TimelinePutError
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|TimelinePutResponse ()
specifier|public
name|TimelinePutResponse
parameter_list|()
block|{    }
comment|/**    * Get a list of {@link TimelinePutError} instances    *     * @return a list of {@link TimelinePutError} instances    */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"errors"
argument_list|)
DECL|method|getErrors ()
specifier|public
name|List
argument_list|<
name|TimelinePutError
argument_list|>
name|getErrors
parameter_list|()
block|{
return|return
name|errors
return|;
block|}
comment|/**    * Add a single {@link TimelinePutError} instance into the existing list    *     * @param error    *          a single {@link TimelinePutError} instance    */
DECL|method|addError (TimelinePutError error)
specifier|public
name|void
name|addError
parameter_list|(
name|TimelinePutError
name|error
parameter_list|)
block|{
name|errors
operator|.
name|add
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a list of {@link TimelinePutError} instances into the existing list    *     * @param errors    *          a list of {@link TimelinePutError} instances    */
DECL|method|addErrors (List<TimelinePutError> errors)
specifier|public
name|void
name|addErrors
parameter_list|(
name|List
argument_list|<
name|TimelinePutError
argument_list|>
name|errors
parameter_list|)
block|{
name|this
operator|.
name|errors
operator|.
name|addAll
argument_list|(
name|errors
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the list to the given list of {@link TimelinePutError} instances    *     * @param errors    *          a list of {@link TimelinePutError} instances    */
DECL|method|setErrors (List<TimelinePutError> errors)
specifier|public
name|void
name|setErrors
parameter_list|(
name|List
argument_list|<
name|TimelinePutError
argument_list|>
name|errors
parameter_list|)
block|{
name|this
operator|.
name|errors
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|errors
operator|.
name|addAll
argument_list|(
name|errors
argument_list|)
expr_stmt|;
block|}
comment|/**    * A class that holds the error code for one entity.    */
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"error"
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
DECL|class|TimelinePutError
specifier|public
specifier|static
class|class
name|TimelinePutError
block|{
comment|/**      * Error code returned when no start time can be found when putting an      * entity. This occurs when the entity does not already exist in the store      * and it is put with no start time or events specified.      */
DECL|field|NO_START_TIME
specifier|public
specifier|static
specifier|final
name|int
name|NO_START_TIME
init|=
literal|1
decl_stmt|;
comment|/**      * Error code returned if an IOException is encountered when putting an      * entity.      */
DECL|field|IO_EXCEPTION
specifier|public
specifier|static
specifier|final
name|int
name|IO_EXCEPTION
init|=
literal|2
decl_stmt|;
comment|/**      * Error code returned if the user specifies the timeline system reserved      * filter key      */
DECL|field|SYSTEM_FILTER_CONFLICT
specifier|public
specifier|static
specifier|final
name|int
name|SYSTEM_FILTER_CONFLICT
init|=
literal|3
decl_stmt|;
comment|/**      * Error code returned if the user is denied to access the timeline data      */
DECL|field|ACCESS_DENIED
specifier|public
specifier|static
specifier|final
name|int
name|ACCESS_DENIED
init|=
literal|4
decl_stmt|;
comment|/**      * Error code returned if the entity doesn't have an valid domain ID      */
DECL|field|NO_DOMAIN
specifier|public
specifier|static
specifier|final
name|int
name|NO_DOMAIN
init|=
literal|5
decl_stmt|;
comment|/**      * Error code returned if the user is denied to relate the entity to another      * one in different domain      */
DECL|field|FORBIDDEN_RELATION
specifier|public
specifier|static
specifier|final
name|int
name|FORBIDDEN_RELATION
init|=
literal|6
decl_stmt|;
comment|/**      * Error code returned if the entity start time is before the eviction      * period of old data.      */
DECL|field|EXPIRED_ENTITY
specifier|public
specifier|static
specifier|final
name|int
name|EXPIRED_ENTITY
init|=
literal|7
decl_stmt|;
DECL|field|entityId
specifier|private
name|String
name|entityId
decl_stmt|;
DECL|field|entityType
specifier|private
name|String
name|entityType
decl_stmt|;
DECL|field|errorCode
specifier|private
name|int
name|errorCode
decl_stmt|;
comment|/**      * Get the entity Id      *       * @return the entity Id      */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"entity"
argument_list|)
DECL|method|getEntityId ()
specifier|public
name|String
name|getEntityId
parameter_list|()
block|{
return|return
name|entityId
return|;
block|}
comment|/**      * Set the entity Id      *       * @param entityId      *          the entity Id      */
DECL|method|setEntityId (String entityId)
specifier|public
name|void
name|setEntityId
parameter_list|(
name|String
name|entityId
parameter_list|)
block|{
name|this
operator|.
name|entityId
operator|=
name|entityId
expr_stmt|;
block|}
comment|/**      * Get the entity type      *       * @return the entity type      */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"entitytype"
argument_list|)
DECL|method|getEntityType ()
specifier|public
name|String
name|getEntityType
parameter_list|()
block|{
return|return
name|entityType
return|;
block|}
comment|/**      * Set the entity type      *       * @param entityType      *          the entity type      */
DECL|method|setEntityType (String entityType)
specifier|public
name|void
name|setEntityType
parameter_list|(
name|String
name|entityType
parameter_list|)
block|{
name|this
operator|.
name|entityType
operator|=
name|entityType
expr_stmt|;
block|}
comment|/**      * Get the error code      *       * @return an error code      */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"errorcode"
argument_list|)
DECL|method|getErrorCode ()
specifier|public
name|int
name|getErrorCode
parameter_list|()
block|{
return|return
name|errorCode
return|;
block|}
comment|/**      * Set the error code to the given error code      *       * @param errorCode      *          an error code      */
DECL|method|setErrorCode (int errorCode)
specifier|public
name|void
name|setErrorCode
parameter_list|(
name|int
name|errorCode
parameter_list|)
block|{
name|this
operator|.
name|errorCode
operator|=
name|errorCode
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

