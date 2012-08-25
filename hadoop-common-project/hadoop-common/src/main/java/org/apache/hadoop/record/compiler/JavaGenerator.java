begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.record.compiler
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|record
operator|.
name|compiler
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
name|io
operator|.
name|IOException
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

begin_comment
comment|/**  * Java Code generator front-end for Hadoop record I/O.  */
end_comment

begin_class
DECL|class|JavaGenerator
class|class
name|JavaGenerator
extends|extends
name|CodeGenerator
block|{
DECL|method|JavaGenerator ()
name|JavaGenerator
parameter_list|()
block|{   }
comment|/**    * Generate Java code for records. This method is only a front-end to    * JRecord, since one file is generated for each record.    *    * @param name possibly full pathname to the file    * @param ilist included files (as JFile)    * @param rlist List of records defined within this file    * @param destDir output directory    */
annotation|@
name|Override
DECL|method|genCode (String name, ArrayList<JFile> ilist, ArrayList<JRecord> rlist, String destDir, ArrayList<String> options)
name|void
name|genCode
parameter_list|(
name|String
name|name
parameter_list|,
name|ArrayList
argument_list|<
name|JFile
argument_list|>
name|ilist
parameter_list|,
name|ArrayList
argument_list|<
name|JRecord
argument_list|>
name|rlist
parameter_list|,
name|String
name|destDir
parameter_list|,
name|ArrayList
argument_list|<
name|String
argument_list|>
name|options
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Iterator
argument_list|<
name|JRecord
argument_list|>
name|iter
init|=
name|rlist
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|JRecord
name|rec
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|rec
operator|.
name|genJavaCode
argument_list|(
name|destDir
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

