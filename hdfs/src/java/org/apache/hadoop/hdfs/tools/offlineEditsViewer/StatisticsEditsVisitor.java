begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools.offlineEditsViewer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|offlineEditsViewer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
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
name|Map
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|FSEditLogOpCodes
import|;
end_import

begin_comment
comment|/**  * StatisticsEditsVisitor implements text version of EditsVisitor  * that aggregates counts of op codes processed  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|StatisticsEditsVisitor
specifier|public
class|class
name|StatisticsEditsVisitor
extends|extends
name|EditsVisitor
block|{
DECL|field|printToScreen
specifier|private
name|boolean
name|printToScreen
init|=
literal|false
decl_stmt|;
DECL|field|okToWrite
specifier|private
name|boolean
name|okToWrite
init|=
literal|false
decl_stmt|;
DECL|field|fw
specifier|final
specifier|private
name|FileWriter
name|fw
decl_stmt|;
DECL|field|opCodeCount
specifier|public
specifier|final
name|Map
argument_list|<
name|FSEditLogOpCodes
argument_list|,
name|Long
argument_list|>
name|opCodeCount
init|=
operator|new
name|HashMap
argument_list|<
name|FSEditLogOpCodes
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Create a processor that writes to the file named.    *    * @param filename Name of file to write output to    */
DECL|method|StatisticsEditsVisitor (String filename, Tokenizer tokenizer)
specifier|public
name|StatisticsEditsVisitor
parameter_list|(
name|String
name|filename
parameter_list|,
name|Tokenizer
name|tokenizer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|filename
argument_list|,
name|tokenizer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a processor that writes to the file named and may or may not    * also output to the screen, as specified.    *    * @param filename Name of file to write output to    * @param tokenizer Input tokenizer    * @param printToScreen Mirror output to screen?    */
DECL|method|StatisticsEditsVisitor (String filename, Tokenizer tokenizer, boolean printToScreen)
specifier|public
name|StatisticsEditsVisitor
parameter_list|(
name|String
name|filename
parameter_list|,
name|Tokenizer
name|tokenizer
parameter_list|,
name|boolean
name|printToScreen
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|tokenizer
argument_list|)
expr_stmt|;
name|this
operator|.
name|printToScreen
operator|=
name|printToScreen
expr_stmt|;
name|fw
operator|=
operator|new
name|FileWriter
argument_list|(
name|filename
argument_list|)
expr_stmt|;
name|okToWrite
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * Start the visitor (initialization)    */
annotation|@
name|Override
DECL|method|start ()
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
comment|// nothing to do
block|}
comment|/* (non-Javadoc)    * @see org.apache.hadoop.hdfs.tools.offlineEditsViewer.EditsVisitor#finish()    */
annotation|@
name|Override
DECL|method|finish ()
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|write
argument_list|(
name|getStatisticsString
argument_list|()
argument_list|)
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.hadoop.hdfs.tools.offlineEditsViewer.EditsVisitor#finishAbnormally()    */
annotation|@
name|Override
DECL|method|finishAbnormally ()
name|void
name|finishAbnormally
parameter_list|()
throws|throws
name|IOException
block|{
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Close output stream and prevent further writing    */
DECL|method|close ()
specifier|private
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|fw
operator|.
name|close
argument_list|()
expr_stmt|;
name|okToWrite
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    * Visit a enclosing element (element that has other elements in it)    */
annotation|@
name|Override
DECL|method|visitEnclosingElement (Tokenizer.Token value)
name|void
name|visitEnclosingElement
parameter_list|(
name|Tokenizer
operator|.
name|Token
name|value
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nothing to do
block|}
comment|/**    * End of eclosing element    */
annotation|@
name|Override
DECL|method|leaveEnclosingElement ()
name|void
name|leaveEnclosingElement
parameter_list|()
throws|throws
name|IOException
block|{
comment|// nothing to do
block|}
comment|/**    * Visit a Token, calculate statistics    *    * @param value a Token to visit    */
annotation|@
name|Override
DECL|method|visit (Tokenizer.Token value)
name|Tokenizer
operator|.
name|Token
name|visit
parameter_list|(
name|Tokenizer
operator|.
name|Token
name|value
parameter_list|)
throws|throws
name|IOException
block|{
comment|// count the opCodes
if|if
condition|(
name|value
operator|.
name|getEditsElement
argument_list|()
operator|==
name|EditsElement
operator|.
name|OPCODE
condition|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|Tokenizer
operator|.
name|ByteToken
condition|)
block|{
name|incrementOpCodeCount
argument_list|(
name|FSEditLogOpCodes
operator|.
name|fromByte
argument_list|(
operator|(
operator|(
name|Tokenizer
operator|.
name|ByteToken
operator|)
name|value
operator|)
operator|.
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Token for EditsElement.OPCODE should be "
operator|+
literal|"of type Tokenizer.ByteToken, not "
operator|+
name|value
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|value
return|;
block|}
comment|/**    * Write parameter to output file (and possibly screen).    *    * @param toWrite Text to write to file    */
DECL|method|write (String toWrite)
specifier|protected
name|void
name|write
parameter_list|(
name|String
name|toWrite
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|okToWrite
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"file not open for writing."
argument_list|)
throw|;
if|if
condition|(
name|printToScreen
condition|)
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|toWrite
argument_list|)
expr_stmt|;
try|try
block|{
name|fw
operator|.
name|write
argument_list|(
name|toWrite
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|okToWrite
operator|=
literal|false
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
comment|/**    * Increment the op code counter    *    * @param opCode opCode for which to increment count    */
DECL|method|incrementOpCodeCount (FSEditLogOpCodes opCode)
specifier|private
name|void
name|incrementOpCodeCount
parameter_list|(
name|FSEditLogOpCodes
name|opCode
parameter_list|)
block|{
if|if
condition|(
operator|!
name|opCodeCount
operator|.
name|containsKey
argument_list|(
name|opCode
argument_list|)
condition|)
block|{
name|opCodeCount
operator|.
name|put
argument_list|(
name|opCode
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
name|Long
name|newValue
init|=
name|opCodeCount
operator|.
name|get
argument_list|(
name|opCode
argument_list|)
operator|+
literal|1
decl_stmt|;
name|opCodeCount
operator|.
name|put
argument_list|(
name|opCode
argument_list|,
name|newValue
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get statistics    *    * @return statistics, map of counts per opCode    */
DECL|method|getStatistics ()
specifier|public
name|Map
argument_list|<
name|FSEditLogOpCodes
argument_list|,
name|Long
argument_list|>
name|getStatistics
parameter_list|()
block|{
return|return
name|opCodeCount
return|;
block|}
comment|/**    * Get the statistics in string format, suitable for printing    *    * @return statistics in in string format, suitable for printing    */
DECL|method|getStatisticsString ()
specifier|public
name|String
name|getStatisticsString
parameter_list|()
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|FSEditLogOpCodes
name|opCode
range|:
name|FSEditLogOpCodes
operator|.
name|values
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"    %-30.30s (%3d): %d%n"
argument_list|,
name|opCode
argument_list|,
name|opCode
operator|.
name|getOpCode
argument_list|()
argument_list|,
name|opCodeCount
operator|.
name|get
argument_list|(
name|opCode
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

