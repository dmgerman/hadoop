begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.lib
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|lib
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|JobConf
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
name|mapred
operator|.
name|JobConfigurable
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
name|mapreduce
operator|.
name|JobContext
import|;
end_import

begin_comment
comment|/**  * This comparator implementation provides a subset of the features provided  * by the Unix/GNU Sort. In particular, the supported features are:  * -n, (Sort numerically)  * -r, (Reverse the result of comparison)  * -k pos1[,pos2], where pos is of the form f[.c][opts], where f is the number  *  of the field to use, and c is the number of the first character from the  *  beginning of the field. Fields and character posns are numbered starting  *  with 1; a character position of zero in pos2 indicates the field's last  *  character. If '.c' is omitted from pos1, it defaults to 1 (the beginning  *  of the field); if omitted from pos2, it defaults to 0 (the end of the  *  field). opts are ordering options (any of 'nr' as described above).   * We assume that the fields in the key are separated by  * {@link JobContext#MAP_OUTPUT_KEY_FIELD_SEPARATOR}  */
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
DECL|class|KeyFieldBasedComparator
specifier|public
class|class
name|KeyFieldBasedComparator
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|partition
operator|.
name|KeyFieldBasedComparator
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
implements|implements
name|JobConfigurable
block|{
DECL|method|configure (JobConf job)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|job
parameter_list|)
block|{
name|super
operator|.
name|setConf
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

