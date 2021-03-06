begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Persistent data formats for the committers.  *  * All of these formats share a base class of  * {@link org.apache.hadoop.fs.s3a.commit.files.PersistentCommitData};  * the subclasses record  *<ol>  *<li>The content of a single pending commit  *   (used by the Magic committer).</li>  *<li>The list of all the files uploaded by a staging committer.</li>  *<li>The summary information saved in the {@code _SUCCESS} file.</li>  *</ol>  *  * There are no guarantees of stability between versions; these are internal  * structures.  *  * The {@link org.apache.hadoop.fs.s3a.commit.files.SuccessData} file is  * the one visible to callers after a job completes; it is an unstable  * manifest intended for testing only.  *  */
end_comment

begin_annotation
annotation|@
name|InterfaceAudience
operator|.
name|Private
end_annotation

begin_annotation
annotation|@
name|InterfaceStability
operator|.
name|Unstable
end_annotation

begin_package
DECL|package|org.apache.hadoop.fs.s3a.commit.files
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|commit
operator|.
name|files
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

end_unit

