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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Splitter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Private
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  *<p><code>ContainerId</code> represents a globally unique identifier  * for a {@link Container} in the cluster.</p>  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|ContainerId
specifier|public
specifier|abstract
class|class
name|ContainerId
implements|implements
name|Comparable
argument_list|<
name|ContainerId
argument_list|>
block|{
DECL|field|_SPLITTER
specifier|private
specifier|static
specifier|final
name|Splitter
name|_SPLITTER
init|=
name|Splitter
operator|.
name|on
argument_list|(
literal|'_'
argument_list|)
operator|.
name|trimResults
argument_list|()
decl_stmt|;
DECL|field|CONTAINER_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|CONTAINER_PREFIX
init|=
literal|"container"
decl_stmt|;
DECL|field|EPOCH_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|EPOCH_PREFIX
init|=
literal|"e"
decl_stmt|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance (ApplicationAttemptId appAttemptId, long containerId)
specifier|public
specifier|static
name|ContainerId
name|newInstance
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|long
name|containerId
parameter_list|)
block|{
name|ContainerId
name|id
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|id
operator|.
name|setContainerId
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|id
operator|.
name|setApplicationAttemptId
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|id
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|id
return|;
block|}
comment|/**    * Get the<code>ApplicationAttemptId</code> of the application to which the    *<code>Container</code> was assigned.    *<p>    * Note: If containers are kept alive across application attempts via    * {@link ApplicationSubmissionContext#setKeepContainersAcrossApplicationAttempts(boolean)}    * the<code>ContainerId</code> does not necessarily contain the current    * running application attempt's<code>ApplicationAttemptId</code> This    * container can be allocated by previously exited application attempt and    * managed by the current running attempt thus have the previous application    * attempt's<code>ApplicationAttemptId</code>.    *</p>    *     * @return<code>ApplicationAttemptId</code> of the application to which the    *<code>Container</code> was assigned    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getApplicationAttemptId ()
specifier|public
specifier|abstract
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setApplicationAttemptId (ApplicationAttemptId atId)
specifier|protected
specifier|abstract
name|void
name|setApplicationAttemptId
parameter_list|(
name|ApplicationAttemptId
name|atId
parameter_list|)
function_decl|;
comment|/**    * Get the lower 32 bits of identifier of the<code>ContainerId</code>,    * which doesn't include epoch. Note that this method will be marked as    * deprecated, so please use<code>getContainerId</code> instead.    * @return lower 32 bits of identifier of the<code>ContainerId</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getId ()
specifier|public
specifier|abstract
name|int
name|getId
parameter_list|()
function_decl|;
comment|/**    * Get the identifier of the<code>ContainerId</code>. Upper 24 bits are    * reserved as epoch of cluster, and lower 40 bits are reserved as    * sequential number of containers.    * @return identifier of the<code>ContainerId</code>    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getContainerId ()
specifier|public
specifier|abstract
name|long
name|getContainerId
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setContainerId (long id)
specifier|protected
specifier|abstract
name|void
name|setContainerId
parameter_list|(
name|long
name|id
parameter_list|)
function_decl|;
comment|// TODO: fail the app submission if attempts are more than 10 or something
DECL|field|appAttemptIdAndEpochFormat
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|NumberFormat
argument_list|>
name|appAttemptIdAndEpochFormat
init|=
operator|new
name|ThreadLocal
argument_list|<
name|NumberFormat
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NumberFormat
name|initialValue
parameter_list|()
block|{
name|NumberFormat
name|fmt
init|=
name|NumberFormat
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|fmt
operator|.
name|setGroupingUsed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|setMinimumIntegerDigits
argument_list|(
literal|2
argument_list|)
expr_stmt|;
return|return
name|fmt
return|;
block|}
block|}
decl_stmt|;
comment|// TODO: Why thread local?
comment|// ^ NumberFormat instances are not threadsafe
DECL|field|containerIdFormat
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|NumberFormat
argument_list|>
name|containerIdFormat
init|=
operator|new
name|ThreadLocal
argument_list|<
name|NumberFormat
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NumberFormat
name|initialValue
parameter_list|()
block|{
name|NumberFormat
name|fmt
init|=
name|NumberFormat
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|fmt
operator|.
name|setGroupingUsed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|setMinimumIntegerDigits
argument_list|(
literal|6
argument_list|)
expr_stmt|;
return|return
name|fmt
return|;
block|}
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|// Generated by IntelliJ IDEA 13.1.
name|int
name|result
init|=
call|(
name|int
call|)
argument_list|(
name|getContainerId
argument_list|()
operator|^
operator|(
name|getContainerId
argument_list|()
operator|>>>
literal|32
operator|)
argument_list|)
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|getApplicationAttemptId
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|ContainerId
name|other
init|=
operator|(
name|ContainerId
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|this
operator|.
name|getContainerId
argument_list|()
operator|!=
name|other
operator|.
name|getContainerId
argument_list|()
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (ContainerId other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|ContainerId
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
operator|==
literal|0
condition|)
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|getContainerId
argument_list|()
argument_list|)
operator|.
name|compareTo
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|other
operator|.
name|getContainerId
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|this
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * @return A string representation of containerId. The format is    * container_e*epoch*_*clusterTimestamp*_*appId*_*attemptId*_*containerId*    * when epoch is larger than 0    * (e.g. container_e17_1410901177871_0001_01_000005).    * *epoch* is increased when RM restarts or fails over.    * When epoch is 0, epoch is omitted    * (e.g. container_1410901177871_0001_01_000005).    */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|CONTAINER_PREFIX
operator|+
literal|"_"
argument_list|)
expr_stmt|;
name|long
name|epoch
init|=
name|getContainerId
argument_list|()
operator|>>
literal|40
decl_stmt|;
if|if
condition|(
name|epoch
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|EPOCH_PREFIX
argument_list|)
operator|.
name|append
argument_list|(
name|appAttemptIdAndEpochFormat
operator|.
name|get
argument_list|()
operator|.
name|format
argument_list|(
name|epoch
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"_"
argument_list|)
expr_stmt|;
empty_stmt|;
block|}
name|ApplicationId
name|appId
init|=
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|appId
operator|.
name|getClusterTimestamp
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"_"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ApplicationId
operator|.
name|appIdFormat
operator|.
name|get
argument_list|()
operator|.
name|format
argument_list|(
name|appId
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"_"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|appAttemptIdAndEpochFormat
operator|.
name|get
argument_list|()
operator|.
name|format
argument_list|(
name|getApplicationAttemptId
argument_list|()
operator|.
name|getAttemptId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"_"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|containerIdFormat
operator|.
name|get
argument_list|()
operator|.
name|format
argument_list|(
literal|0xffffffffffL
operator|&
name|getContainerId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|fromString (String containerIdStr)
specifier|public
specifier|static
name|ContainerId
name|fromString
parameter_list|(
name|String
name|containerIdStr
parameter_list|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|_SPLITTER
operator|.
name|split
argument_list|(
name|containerIdStr
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|it
operator|.
name|next
argument_list|()
operator|.
name|equals
argument_list|(
name|CONTAINER_PREFIX
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid ContainerId prefix: "
operator|+
name|containerIdStr
argument_list|)
throw|;
block|}
try|try
block|{
name|String
name|epochOrClusterTimestampStr
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|long
name|epoch
init|=
literal|0
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptID
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|epochOrClusterTimestampStr
operator|.
name|startsWith
argument_list|(
name|EPOCH_PREFIX
argument_list|)
condition|)
block|{
name|String
name|epochStr
init|=
name|epochOrClusterTimestampStr
decl_stmt|;
name|epoch
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|epochStr
operator|.
name|substring
argument_list|(
name|EPOCH_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|appAttemptID
operator|=
name|toApplicationAttemptId
argument_list|(
name|it
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|clusterTimestampStr
init|=
name|epochOrClusterTimestampStr
decl_stmt|;
name|long
name|clusterTimestamp
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|clusterTimestampStr
argument_list|)
decl_stmt|;
name|appAttemptID
operator|=
name|toApplicationAttemptId
argument_list|(
name|clusterTimestamp
argument_list|,
name|it
argument_list|)
expr_stmt|;
block|}
name|long
name|id
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|cid
init|=
operator|(
name|epoch
operator|<<
literal|40
operator|)
operator||
name|id
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|ContainerId
operator|.
name|newInstance
argument_list|(
name|appAttemptID
argument_list|,
name|cid
argument_list|)
decl_stmt|;
return|return
name|containerId
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|n
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid ContainerId: "
operator|+
name|containerIdStr
argument_list|,
name|n
argument_list|)
throw|;
block|}
block|}
DECL|method|toApplicationAttemptId ( Iterator<String> it)
specifier|private
specifier|static
name|ApplicationAttemptId
name|toApplicationAttemptId
parameter_list|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
parameter_list|)
throws|throws
name|NumberFormatException
block|{
return|return
name|toApplicationAttemptId
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
name|it
argument_list|)
return|;
block|}
DECL|method|toApplicationAttemptId ( long clusterTimestamp, Iterator<String> it)
specifier|private
specifier|static
name|ApplicationAttemptId
name|toApplicationAttemptId
parameter_list|(
name|long
name|clusterTimestamp
parameter_list|,
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
parameter_list|)
throws|throws
name|NumberFormatException
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|clusterTimestamp
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|appAttemptId
return|;
block|}
DECL|method|build ()
specifier|protected
specifier|abstract
name|void
name|build
parameter_list|()
function_decl|;
block|}
end_class

end_unit

