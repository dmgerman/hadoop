begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

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
name|EnumMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|WritableUtils
import|;
end_import

begin_comment
comment|/**  * TaskID represents the immutable and unique identifier for   * a Map or Reduce Task. Each TaskID encompasses multiple attempts made to  * execute the Map or Reduce Task, each of which are uniquely indentified by  * their TaskAttemptID.  *   * TaskID consists of 3 parts. First part is the {@link JobID}, that this   * TaskInProgress belongs to. Second part of the TaskID is either 'm' or 'r'   * representing whether the task is a map task or a reduce task.   * And the third part is the task number.<br>   * An example TaskID is :   *<code>task_200707121733_0003_m_000005</code> , which represents the  * fifth map task in the third job running at the jobtracker   * started at<code>200707121733</code>.   *<p>  * Applications should never construct or parse TaskID strings  * , but rather use appropriate constructors or {@link #forName(String)}   * method.   *   * @see JobID  * @see TaskAttemptID  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|TaskID
specifier|public
class|class
name|TaskID
extends|extends
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|ID
block|{
DECL|field|TASK
specifier|protected
specifier|static
specifier|final
name|String
name|TASK
init|=
literal|"task"
decl_stmt|;
DECL|field|idFormat
specifier|protected
specifier|static
specifier|final
name|NumberFormat
name|idFormat
init|=
name|NumberFormat
operator|.
name|getInstance
argument_list|()
decl_stmt|;
static|static
block|{
name|idFormat
operator|.
name|setGroupingUsed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|idFormat
operator|.
name|setMinimumIntegerDigits
argument_list|(
literal|6
argument_list|)
expr_stmt|;
block|}
DECL|field|jobId
specifier|private
name|JobID
name|jobId
decl_stmt|;
DECL|field|type
specifier|private
name|TaskType
name|type
decl_stmt|;
comment|/**    * Constructs a TaskID object from given {@link JobID}.      * @param jobId JobID that this tip belongs to     * @param type the {@link TaskType} of the task     * @param id the tip number    */
DECL|method|TaskID (JobID jobId, TaskType type, int id)
specifier|public
name|TaskID
parameter_list|(
name|JobID
name|jobId
parameter_list|,
name|TaskType
name|type
parameter_list|,
name|int
name|id
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|jobId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"jobId cannot be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|jobId
operator|=
name|jobId
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**    * Constructs a TaskInProgressId object from given parts.    * @param jtIdentifier jobTracker identifier    * @param jobId job number     * @param type the TaskType     * @param id the tip number    */
DECL|method|TaskID (String jtIdentifier, int jobId, TaskType type, int id)
specifier|public
name|TaskID
parameter_list|(
name|String
name|jtIdentifier
parameter_list|,
name|int
name|jobId
parameter_list|,
name|TaskType
name|type
parameter_list|,
name|int
name|id
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|JobID
argument_list|(
name|jtIdentifier
argument_list|,
name|jobId
argument_list|)
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
DECL|method|TaskID ()
specifier|public
name|TaskID
parameter_list|()
block|{
name|jobId
operator|=
operator|new
name|JobID
argument_list|()
expr_stmt|;
block|}
comment|/** Returns the {@link JobID} object that this tip belongs to */
DECL|method|getJobID ()
specifier|public
name|JobID
name|getJobID
parameter_list|()
block|{
return|return
name|jobId
return|;
block|}
comment|/**Returns whether this TaskID is a map ID */
annotation|@
name|Deprecated
DECL|method|isMap ()
specifier|public
name|boolean
name|isMap
parameter_list|()
block|{
return|return
name|type
operator|==
name|TaskType
operator|.
name|MAP
return|;
block|}
comment|/**    * Get the type of the task    */
DECL|method|getTaskType ()
specifier|public
name|TaskType
name|getTaskType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|TaskID
name|that
init|=
operator|(
name|TaskID
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|type
operator|==
name|that
operator|.
name|type
operator|&&
name|this
operator|.
name|jobId
operator|.
name|equals
argument_list|(
name|that
operator|.
name|jobId
argument_list|)
return|;
block|}
comment|/**Compare TaskInProgressIds by first jobIds, then by tip numbers. Reduces are     * defined as greater then maps.*/
annotation|@
name|Override
DECL|method|compareTo (ID o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|ID
name|o
parameter_list|)
block|{
name|TaskID
name|that
init|=
operator|(
name|TaskID
operator|)
name|o
decl_stmt|;
name|int
name|jobComp
init|=
name|this
operator|.
name|jobId
operator|.
name|compareTo
argument_list|(
name|that
operator|.
name|jobId
argument_list|)
decl_stmt|;
if|if
condition|(
name|jobComp
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|type
operator|==
name|that
operator|.
name|type
condition|)
block|{
return|return
name|this
operator|.
name|id
operator|-
name|that
operator|.
name|id
return|;
block|}
else|else
block|{
return|return
name|this
operator|.
name|type
operator|.
name|compareTo
argument_list|(
name|that
operator|.
name|type
argument_list|)
return|;
block|}
block|}
else|else
return|return
name|jobComp
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|appendTo
argument_list|(
operator|new
name|StringBuilder
argument_list|(
name|TASK
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Add the unique string to the given builder.    * @param builder the builder to append to    * @return the builder that was passed in    */
DECL|method|appendTo (StringBuilder builder)
specifier|protected
name|StringBuilder
name|appendTo
parameter_list|(
name|StringBuilder
name|builder
parameter_list|)
block|{
return|return
name|jobId
operator|.
name|appendTo
argument_list|(
name|builder
argument_list|)
operator|.
name|append
argument_list|(
name|SEPARATOR
argument_list|)
operator|.
name|append
argument_list|(
name|CharTaskTypeMaps
operator|.
name|getRepresentingCharacter
argument_list|(
name|type
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|SEPARATOR
argument_list|)
operator|.
name|append
argument_list|(
name|idFormat
operator|.
name|format
argument_list|(
name|id
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|jobId
operator|.
name|hashCode
argument_list|()
operator|*
literal|524287
operator|+
name|id
return|;
block|}
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|jobId
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|type
operator|=
name|WritableUtils
operator|.
name|readEnum
argument_list|(
name|in
argument_list|,
name|TaskType
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|jobId
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeEnum
argument_list|(
name|out
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
comment|/** Construct a TaskID object from given string     * @return constructed TaskID object or null if the given String is null    * @throws IllegalArgumentException if the given string is malformed    */
DECL|method|forName (String str)
specifier|public
specifier|static
name|TaskID
name|forName
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|str
operator|==
literal|null
condition|)
return|return
literal|null
return|;
try|try
block|{
name|String
index|[]
name|parts
init|=
name|str
operator|.
name|split
argument_list|(
literal|"_"
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|length
operator|==
literal|5
condition|)
block|{
if|if
condition|(
name|parts
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|TASK
argument_list|)
condition|)
block|{
name|String
name|type
init|=
name|parts
index|[
literal|3
index|]
decl_stmt|;
name|TaskType
name|t
init|=
name|CharTaskTypeMaps
operator|.
name|getTaskType
argument_list|(
name|type
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|TaskID
argument_list|(
name|parts
index|[
literal|1
index|]
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|parts
index|[
literal|2
index|]
argument_list|)
argument_list|,
name|t
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|parts
index|[
literal|4
index|]
argument_list|)
argument_list|)
return|;
block|}
else|else
throw|throw
operator|new
name|Exception
argument_list|()
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|//fall below
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"TaskId string : "
operator|+
name|str
operator|+
literal|" is not properly formed"
argument_list|)
throw|;
block|}
comment|/**    * Gets the character representing the {@link TaskType}    * @param type the TaskType    * @return the character    */
DECL|method|getRepresentingCharacter (TaskType type)
specifier|public
specifier|static
name|char
name|getRepresentingCharacter
parameter_list|(
name|TaskType
name|type
parameter_list|)
block|{
return|return
name|CharTaskTypeMaps
operator|.
name|getRepresentingCharacter
argument_list|(
name|type
argument_list|)
return|;
block|}
comment|/**    * Gets the {@link TaskType} corresponding to the character    * @param c the character    * @return the TaskType    */
DECL|method|getTaskType (char c)
specifier|public
specifier|static
name|TaskType
name|getTaskType
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
name|CharTaskTypeMaps
operator|.
name|getTaskType
argument_list|(
name|c
argument_list|)
return|;
block|}
DECL|method|getAllTaskTypes ()
specifier|public
specifier|static
name|String
name|getAllTaskTypes
parameter_list|()
block|{
return|return
name|CharTaskTypeMaps
operator|.
name|allTaskTypes
return|;
block|}
comment|/**    * Maintains the mapping from the character representation of a task type to     * the enum class TaskType constants    */
DECL|class|CharTaskTypeMaps
specifier|static
class|class
name|CharTaskTypeMaps
block|{
DECL|field|typeToCharMap
specifier|private
specifier|static
name|EnumMap
argument_list|<
name|TaskType
argument_list|,
name|Character
argument_list|>
name|typeToCharMap
init|=
operator|new
name|EnumMap
argument_list|<
name|TaskType
argument_list|,
name|Character
argument_list|>
argument_list|(
name|TaskType
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|charToTypeMap
specifier|private
specifier|static
name|Map
argument_list|<
name|Character
argument_list|,
name|TaskType
argument_list|>
name|charToTypeMap
init|=
operator|new
name|HashMap
argument_list|<
name|Character
argument_list|,
name|TaskType
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|allTaskTypes
specifier|static
name|String
name|allTaskTypes
init|=
literal|"(m|r|s|c|t)"
decl_stmt|;
static|static
block|{
name|setupTaskTypeToCharMapping
argument_list|()
expr_stmt|;
name|setupCharToTaskTypeMapping
argument_list|()
expr_stmt|;
block|}
DECL|method|setupTaskTypeToCharMapping ()
specifier|private
specifier|static
name|void
name|setupTaskTypeToCharMapping
parameter_list|()
block|{
name|typeToCharMap
operator|.
name|put
argument_list|(
name|TaskType
operator|.
name|MAP
argument_list|,
literal|'m'
argument_list|)
expr_stmt|;
name|typeToCharMap
operator|.
name|put
argument_list|(
name|TaskType
operator|.
name|REDUCE
argument_list|,
literal|'r'
argument_list|)
expr_stmt|;
name|typeToCharMap
operator|.
name|put
argument_list|(
name|TaskType
operator|.
name|JOB_SETUP
argument_list|,
literal|'s'
argument_list|)
expr_stmt|;
name|typeToCharMap
operator|.
name|put
argument_list|(
name|TaskType
operator|.
name|JOB_CLEANUP
argument_list|,
literal|'c'
argument_list|)
expr_stmt|;
name|typeToCharMap
operator|.
name|put
argument_list|(
name|TaskType
operator|.
name|TASK_CLEANUP
argument_list|,
literal|'t'
argument_list|)
expr_stmt|;
block|}
DECL|method|setupCharToTaskTypeMapping ()
specifier|private
specifier|static
name|void
name|setupCharToTaskTypeMapping
parameter_list|()
block|{
name|charToTypeMap
operator|.
name|put
argument_list|(
literal|'m'
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|)
expr_stmt|;
name|charToTypeMap
operator|.
name|put
argument_list|(
literal|'r'
argument_list|,
name|TaskType
operator|.
name|REDUCE
argument_list|)
expr_stmt|;
name|charToTypeMap
operator|.
name|put
argument_list|(
literal|'s'
argument_list|,
name|TaskType
operator|.
name|JOB_SETUP
argument_list|)
expr_stmt|;
name|charToTypeMap
operator|.
name|put
argument_list|(
literal|'c'
argument_list|,
name|TaskType
operator|.
name|JOB_CLEANUP
argument_list|)
expr_stmt|;
name|charToTypeMap
operator|.
name|put
argument_list|(
literal|'t'
argument_list|,
name|TaskType
operator|.
name|TASK_CLEANUP
argument_list|)
expr_stmt|;
block|}
DECL|method|getRepresentingCharacter (TaskType type)
specifier|static
name|char
name|getRepresentingCharacter
parameter_list|(
name|TaskType
name|type
parameter_list|)
block|{
return|return
name|typeToCharMap
operator|.
name|get
argument_list|(
name|type
argument_list|)
return|;
block|}
DECL|method|getTaskType (char c)
specifier|static
name|TaskType
name|getTaskType
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
name|charToTypeMap
operator|.
name|get
argument_list|(
name|c
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

