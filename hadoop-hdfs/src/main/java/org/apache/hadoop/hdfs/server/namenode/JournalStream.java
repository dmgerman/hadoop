begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
package|;
end_package

begin_comment
comment|/**  * A generic interface for journal input and output streams.  */
end_comment

begin_interface
DECL|interface|JournalStream
interface|interface
name|JournalStream
block|{
comment|/**    * Type of the underlying persistent storage type the stream is based upon.    *<ul>    *<li>{@link JournalType#FILE} - streams edits into a local file, see    * {@link FSEditLog.EditLogFileOutputStream} and     * {@link FSEditLog.EditLogFileInputStream}</li>    *<li>{@link JournalType#BACKUP} - streams edits to a backup node, see    * {@link EditLogBackupOutputStream} and {@link EditLogBackupInputStream}</li>    *</ul>    */
DECL|enum|JournalType
specifier|static
enum|enum
name|JournalType
block|{
DECL|enumConstant|FILE
name|FILE
block|,
DECL|enumConstant|BACKUP
name|BACKUP
block|;
DECL|method|isOfType (JournalType other)
name|boolean
name|isOfType
parameter_list|(
name|JournalType
name|other
parameter_list|)
block|{
return|return
name|other
operator|==
literal|null
operator|||
name|this
operator|==
name|other
return|;
block|}
block|}
empty_stmt|;
comment|/**    * Get this stream name.    *     * @return name of the stream    */
DECL|method|getName ()
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * Get the type of the stream.    * Determines the underlying persistent storage type.    * @see JournalType    * @return type    */
DECL|method|getType ()
name|JournalType
name|getType
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

