begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|io
operator|.
name|InputStream
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
name|jobhistory
operator|.
name|EventReader
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
name|jobhistory
operator|.
name|HistoryEvent
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
name|jobhistory
operator|.
name|JobHistory
import|;
end_import

begin_comment
comment|/**  * {@link JobHistoryParser} that parses {@link JobHistory} files produced by  * {@link org.apache.hadoop.mapreduce.jobhistory.JobHistory} in the same source  * code tree as rumen.  */
end_comment

begin_class
DECL|class|CurrentJHParser
specifier|public
class|class
name|CurrentJHParser
implements|implements
name|JobHistoryParser
block|{
DECL|field|reader
specifier|private
name|EventReader
name|reader
decl_stmt|;
DECL|class|ForkedDataInputStream
specifier|private
specifier|static
class|class
name|ForkedDataInputStream
extends|extends
name|DataInputStream
block|{
DECL|method|ForkedDataInputStream (InputStream input)
name|ForkedDataInputStream
parameter_list|(
name|InputStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// no code
block|}
block|}
comment|/**    * Can this parser parse the input?    *     * @param input    * @return Whether this parser can parse the input.    * @throws IOException    */
DECL|method|canParse (InputStream input)
specifier|public
specifier|static
name|boolean
name|canParse
parameter_list|(
name|InputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DataInputStream
name|in
init|=
operator|new
name|ForkedDataInputStream
argument_list|(
name|input
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|EventReader
name|reader
init|=
operator|new
name|EventReader
argument_list|(
name|in
argument_list|)
decl_stmt|;
try|try
block|{
name|reader
operator|.
name|getNextEvent
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|CurrentJHParser (InputStream input)
specifier|public
name|CurrentJHParser
parameter_list|(
name|InputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|reader
operator|=
operator|new
name|EventReader
argument_list|(
operator|new
name|DataInputStream
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nextEvent ()
specifier|public
name|HistoryEvent
name|nextEvent
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|getNextEvent
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

