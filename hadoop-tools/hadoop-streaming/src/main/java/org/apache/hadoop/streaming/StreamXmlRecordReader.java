begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.streaming
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|streaming
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|*
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
name|DataOutputBuffer
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
name|Writable
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
name|Text
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
name|WritableComparable
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|FSDataInputStream
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
name|Reporter
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
name|FileSplit
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

begin_comment
comment|/** A way to interpret XML fragments as Mapper input records.  *  Values are XML subtrees delimited by configurable tags.  *  Keys could be the value of a certain attribute in the XML subtree,   *  but this is left to the stream processor application.  *  *  The name-value properties that StreamXmlRecordReader understands are:  *    String begin (chars marking beginning of record)  *    String end   (chars marking end of record)  *    int maxrec   (maximum record size)  *    int lookahead(maximum lookahead to sync CDATA)  *    boolean slowmatch  */
end_comment

begin_class
DECL|class|StreamXmlRecordReader
specifier|public
class|class
name|StreamXmlRecordReader
extends|extends
name|StreamBaseRecordReader
block|{
DECL|method|StreamXmlRecordReader (FSDataInputStream in, FileSplit split, Reporter reporter, JobConf job, FileSystem fs)
specifier|public
name|StreamXmlRecordReader
parameter_list|(
name|FSDataInputStream
name|in
parameter_list|,
name|FileSplit
name|split
parameter_list|,
name|Reporter
name|reporter
parameter_list|,
name|JobConf
name|job
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|,
name|split
argument_list|,
name|reporter
argument_list|,
name|job
argument_list|,
name|fs
argument_list|)
expr_stmt|;
name|beginMark_
operator|=
name|checkJobGet
argument_list|(
name|CONF_NS
operator|+
literal|"begin"
argument_list|)
expr_stmt|;
name|endMark_
operator|=
name|checkJobGet
argument_list|(
name|CONF_NS
operator|+
literal|"end"
argument_list|)
expr_stmt|;
name|maxRecSize_
operator|=
name|job_
operator|.
name|getInt
argument_list|(
name|CONF_NS
operator|+
literal|"maxrec"
argument_list|,
literal|50
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|lookAhead_
operator|=
name|job_
operator|.
name|getInt
argument_list|(
name|CONF_NS
operator|+
literal|"lookahead"
argument_list|,
literal|2
operator|*
name|maxRecSize_
argument_list|)
expr_stmt|;
name|synched_
operator|=
literal|false
expr_stmt|;
name|slowMatch_
operator|=
name|job_
operator|.
name|getBoolean
argument_list|(
name|CONF_NS
operator|+
literal|"slowmatch"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|slowMatch_
condition|)
block|{
name|beginPat_
operator|=
name|makePatternCDataOrMark
argument_list|(
name|beginMark_
argument_list|)
expr_stmt|;
name|endPat_
operator|=
name|makePatternCDataOrMark
argument_list|(
name|endMark_
argument_list|)
expr_stmt|;
block|}
name|init
argument_list|()
expr_stmt|;
block|}
DECL|method|init ()
specifier|public
specifier|final
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"StreamBaseRecordReader.init: "
operator|+
literal|" start_="
operator|+
name|start_
operator|+
literal|" end_="
operator|+
name|end_
operator|+
literal|" length_="
operator|+
name|length_
operator|+
literal|" start_> in_.getPos() ="
operator|+
operator|(
name|start_
operator|>
name|in_
operator|.
name|getPos
argument_list|()
operator|)
operator|+
literal|" "
operator|+
name|start_
operator|+
literal|"> "
operator|+
name|in_
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|start_
operator|>
name|in_
operator|.
name|getPos
argument_list|()
condition|)
block|{
name|in_
operator|.
name|seek
argument_list|(
name|start_
argument_list|)
expr_stmt|;
block|}
name|pos_
operator|=
name|start_
expr_stmt|;
name|bin_
operator|=
operator|new
name|BufferedInputStream
argument_list|(
name|in_
argument_list|)
expr_stmt|;
name|seekNextRecordBoundary
argument_list|()
expr_stmt|;
block|}
DECL|field|numNext
name|int
name|numNext
init|=
literal|0
decl_stmt|;
DECL|method|next (Text key, Text value)
specifier|public
specifier|synchronized
name|boolean
name|next
parameter_list|(
name|Text
name|key
parameter_list|,
name|Text
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|numNext
operator|++
expr_stmt|;
if|if
condition|(
name|pos_
operator|>=
name|end_
condition|)
block|{
return|return
literal|false
return|;
block|}
name|DataOutputBuffer
name|buf
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|readUntilMatchBegin
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|pos_
operator|>=
name|end_
operator|||
operator|!
name|readUntilMatchEnd
argument_list|(
name|buf
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// There is only one elem..key/value splitting is not done here.
name|byte
index|[]
name|record
init|=
operator|new
name|byte
index|[
name|buf
operator|.
name|getLength
argument_list|()
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buf
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|record
argument_list|,
literal|0
argument_list|,
name|record
operator|.
name|length
argument_list|)
expr_stmt|;
name|numRecStats
argument_list|(
name|record
argument_list|,
literal|0
argument_list|,
name|record
operator|.
name|length
argument_list|)
expr_stmt|;
name|key
operator|.
name|set
argument_list|(
name|record
argument_list|)
expr_stmt|;
name|value
operator|.
name|set
argument_list|(
literal|""
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|seekNextRecordBoundary ()
specifier|public
name|void
name|seekNextRecordBoundary
parameter_list|()
throws|throws
name|IOException
block|{
name|readUntilMatchBegin
argument_list|()
expr_stmt|;
block|}
DECL|method|readUntilMatchBegin ()
name|boolean
name|readUntilMatchBegin
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|slowMatch_
condition|)
block|{
return|return
name|slowReadUntilMatch
argument_list|(
name|beginPat_
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|fastReadUntilMatch
argument_list|(
name|beginMark_
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
DECL|method|readUntilMatchEnd (DataOutputBuffer buf)
specifier|private
name|boolean
name|readUntilMatchEnd
parameter_list|(
name|DataOutputBuffer
name|buf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|slowMatch_
condition|)
block|{
return|return
name|slowReadUntilMatch
argument_list|(
name|endPat_
argument_list|,
literal|true
argument_list|,
name|buf
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|fastReadUntilMatch
argument_list|(
name|endMark_
argument_list|,
literal|true
argument_list|,
name|buf
argument_list|)
return|;
block|}
block|}
DECL|method|slowReadUntilMatch (Pattern markPattern, boolean includePat, DataOutputBuffer outBufOrNull)
specifier|private
name|boolean
name|slowReadUntilMatch
parameter_list|(
name|Pattern
name|markPattern
parameter_list|,
name|boolean
name|includePat
parameter_list|,
name|DataOutputBuffer
name|outBufOrNull
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|Math
operator|.
name|max
argument_list|(
name|lookAhead_
argument_list|,
name|maxRecSize_
argument_list|)
index|]
decl_stmt|;
name|int
name|read
init|=
literal|0
decl_stmt|;
name|bin_
operator|.
name|mark
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|lookAhead_
argument_list|,
name|maxRecSize_
argument_list|)
operator|+
literal|2
argument_list|)
expr_stmt|;
comment|//mark to invalidate if we read more
name|read
operator|=
name|bin_
operator|.
name|read
argument_list|(
name|buf
argument_list|)
expr_stmt|;
if|if
condition|(
name|read
operator|==
operator|-
literal|1
condition|)
return|return
literal|false
return|;
name|String
name|sbuf
init|=
operator|new
name|String
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|read
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|Matcher
name|match
init|=
name|markPattern
operator|.
name|matcher
argument_list|(
name|sbuf
argument_list|)
decl_stmt|;
name|firstMatchStart_
operator|=
name|NA
expr_stmt|;
name|firstMatchEnd_
operator|=
name|NA
expr_stmt|;
name|int
name|bufPos
init|=
literal|0
decl_stmt|;
name|int
name|state
init|=
name|synched_
condition|?
name|CDATA_OUT
else|:
name|CDATA_UNK
decl_stmt|;
name|int
name|s
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|match
operator|.
name|find
argument_list|(
name|bufPos
argument_list|)
condition|)
block|{
name|int
name|input
decl_stmt|;
if|if
condition|(
name|match
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|input
operator|=
name|CDATA_BEGIN
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|match
operator|.
name|group
argument_list|(
literal|2
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|input
operator|=
name|CDATA_END
expr_stmt|;
name|firstMatchStart_
operator|=
name|NA
expr_stmt|;
comment|// |<DOC CDATA[</DOC> ]]> should keep it
block|}
else|else
block|{
name|input
operator|=
name|RECORD_MAYBE
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|==
name|RECORD_MAYBE
condition|)
block|{
if|if
condition|(
name|firstMatchStart_
operator|==
name|NA
condition|)
block|{
name|firstMatchStart_
operator|=
name|match
operator|.
name|start
argument_list|()
expr_stmt|;
name|firstMatchEnd_
operator|=
name|match
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
block|}
name|state
operator|=
name|nextState
argument_list|(
name|state
argument_list|,
name|input
argument_list|,
name|match
operator|.
name|start
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|==
name|RECORD_ACCEPT
condition|)
block|{
break|break;
block|}
name|bufPos
operator|=
name|match
operator|.
name|end
argument_list|()
expr_stmt|;
name|s
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|state
operator|!=
name|CDATA_UNK
condition|)
block|{
name|synched_
operator|=
literal|true
expr_stmt|;
block|}
name|boolean
name|matched
init|=
operator|(
name|firstMatchStart_
operator|!=
name|NA
operator|)
operator|&&
operator|(
name|state
operator|==
name|RECORD_ACCEPT
operator|||
name|state
operator|==
name|CDATA_UNK
operator|)
decl_stmt|;
if|if
condition|(
name|matched
condition|)
block|{
name|int
name|endPos
init|=
name|includePat
condition|?
name|firstMatchEnd_
else|:
name|firstMatchStart_
decl_stmt|;
name|bin_
operator|.
name|reset
argument_list|()
expr_stmt|;
for|for
control|(
name|long
name|skiplen
init|=
name|endPos
init|;
name|skiplen
operator|>
literal|0
condition|;
control|)
block|{
name|skiplen
operator|-=
name|bin_
operator|.
name|skip
argument_list|(
name|skiplen
argument_list|)
expr_stmt|;
comment|// Skip succeeds as we have read this buffer
block|}
name|pos_
operator|+=
name|endPos
expr_stmt|;
if|if
condition|(
name|outBufOrNull
operator|!=
literal|null
condition|)
block|{
name|outBufOrNull
operator|.
name|writeBytes
argument_list|(
name|sbuf
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|endPos
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|matched
return|;
block|}
comment|// states
DECL|field|CDATA_IN
specifier|private
specifier|final
specifier|static
name|int
name|CDATA_IN
init|=
literal|10
decl_stmt|;
DECL|field|CDATA_OUT
specifier|private
specifier|final
specifier|static
name|int
name|CDATA_OUT
init|=
literal|11
decl_stmt|;
DECL|field|CDATA_UNK
specifier|private
specifier|final
specifier|static
name|int
name|CDATA_UNK
init|=
literal|12
decl_stmt|;
DECL|field|RECORD_ACCEPT
specifier|private
specifier|final
specifier|static
name|int
name|RECORD_ACCEPT
init|=
literal|13
decl_stmt|;
comment|// inputs
DECL|field|CDATA_BEGIN
specifier|private
specifier|final
specifier|static
name|int
name|CDATA_BEGIN
init|=
literal|20
decl_stmt|;
DECL|field|CDATA_END
specifier|private
specifier|final
specifier|static
name|int
name|CDATA_END
init|=
literal|21
decl_stmt|;
DECL|field|RECORD_MAYBE
specifier|private
specifier|final
specifier|static
name|int
name|RECORD_MAYBE
init|=
literal|22
decl_stmt|;
comment|/* also updates firstMatchStart_;*/
DECL|method|nextState (int state, int input, int bufPos)
name|int
name|nextState
parameter_list|(
name|int
name|state
parameter_list|,
name|int
name|input
parameter_list|,
name|int
name|bufPos
parameter_list|)
block|{
switch|switch
condition|(
name|state
condition|)
block|{
case|case
name|CDATA_UNK
case|:
case|case
name|CDATA_OUT
case|:
switch|switch
condition|(
name|input
condition|)
block|{
case|case
name|CDATA_BEGIN
case|:
return|return
name|CDATA_IN
return|;
case|case
name|CDATA_END
case|:
if|if
condition|(
name|state
operator|==
name|CDATA_OUT
condition|)
block|{
comment|//System.out.println("buggy XML " + bufPos);
block|}
return|return
name|CDATA_OUT
return|;
case|case
name|RECORD_MAYBE
case|:
return|return
operator|(
name|state
operator|==
name|CDATA_UNK
operator|)
condition|?
name|CDATA_UNK
else|:
name|RECORD_ACCEPT
return|;
block|}
break|break;
case|case
name|CDATA_IN
case|:
return|return
operator|(
name|input
operator|==
name|CDATA_END
operator|)
condition|?
name|CDATA_OUT
else|:
name|CDATA_IN
return|;
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|state
operator|+
literal|" "
operator|+
name|input
operator|+
literal|" "
operator|+
name|bufPos
operator|+
literal|" "
operator|+
name|splitName_
argument_list|)
throw|;
block|}
DECL|method|makePatternCDataOrMark (String escapedMark)
name|Pattern
name|makePatternCDataOrMark
parameter_list|(
name|String
name|escapedMark
parameter_list|)
block|{
name|StringBuffer
name|pat
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|addGroup
argument_list|(
name|pat
argument_list|,
name|StreamUtil
operator|.
name|regexpEscape
argument_list|(
literal|"CDATA["
argument_list|)
argument_list|)
expr_stmt|;
comment|// CDATA_BEGIN
name|addGroup
argument_list|(
name|pat
argument_list|,
name|StreamUtil
operator|.
name|regexpEscape
argument_list|(
literal|"]]>"
argument_list|)
argument_list|)
expr_stmt|;
comment|// CDATA_END
name|addGroup
argument_list|(
name|pat
argument_list|,
name|escapedMark
argument_list|)
expr_stmt|;
comment|// RECORD_MAYBE
return|return
name|Pattern
operator|.
name|compile
argument_list|(
name|pat
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|addGroup (StringBuffer pat, String escapedGroup)
name|void
name|addGroup
parameter_list|(
name|StringBuffer
name|pat
parameter_list|,
name|String
name|escapedGroup
parameter_list|)
block|{
if|if
condition|(
name|pat
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|pat
operator|.
name|append
argument_list|(
literal|"|"
argument_list|)
expr_stmt|;
block|}
name|pat
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|pat
operator|.
name|append
argument_list|(
name|escapedGroup
argument_list|)
expr_stmt|;
name|pat
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
DECL|method|fastReadUntilMatch (String textPat, boolean includePat, DataOutputBuffer outBufOrNull)
name|boolean
name|fastReadUntilMatch
parameter_list|(
name|String
name|textPat
parameter_list|,
name|boolean
name|includePat
parameter_list|,
name|DataOutputBuffer
name|outBufOrNull
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|cpat
init|=
name|textPat
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|int
name|m
init|=
literal|0
decl_stmt|;
name|boolean
name|match
init|=
literal|false
decl_stmt|;
name|int
name|msup
init|=
name|cpat
operator|.
name|length
decl_stmt|;
name|int
name|LL
init|=
literal|120000
operator|*
literal|10
decl_stmt|;
name|bin_
operator|.
name|mark
argument_list|(
name|LL
argument_list|)
expr_stmt|;
comment|// large number to invalidate mark
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|b
init|=
name|bin_
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|b
operator|==
operator|-
literal|1
condition|)
break|break;
name|byte
name|c
init|=
operator|(
name|byte
operator|)
name|b
decl_stmt|;
comment|// this assumes eight-bit matching. OK with UTF-8
if|if
condition|(
name|c
operator|==
name|cpat
index|[
name|m
index|]
condition|)
block|{
name|m
operator|++
expr_stmt|;
if|if
condition|(
name|m
operator|==
name|msup
condition|)
block|{
name|match
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
else|else
block|{
name|bin_
operator|.
name|mark
argument_list|(
name|LL
argument_list|)
expr_stmt|;
comment|// rest mark so we could jump back if we found a match
if|if
condition|(
name|outBufOrNull
operator|!=
literal|null
condition|)
block|{
name|outBufOrNull
operator|.
name|write
argument_list|(
name|cpat
argument_list|,
literal|0
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|outBufOrNull
operator|.
name|write
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
name|pos_
operator|+=
name|m
operator|+
literal|1
expr_stmt|;
comment|// skip m chars, +1 for 'c'
name|m
operator|=
literal|0
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|includePat
operator|&&
name|match
condition|)
block|{
name|bin_
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|outBufOrNull
operator|!=
literal|null
condition|)
block|{
name|outBufOrNull
operator|.
name|write
argument_list|(
name|cpat
argument_list|)
expr_stmt|;
name|pos_
operator|+=
name|msup
expr_stmt|;
block|}
return|return
name|match
return|;
block|}
DECL|method|checkJobGet (String prop)
name|String
name|checkJobGet
parameter_list|(
name|String
name|prop
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|val
init|=
name|job_
operator|.
name|get
argument_list|(
name|prop
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"JobConf: missing required property: "
operator|+
name|prop
argument_list|)
throw|;
block|}
return|return
name|val
return|;
block|}
DECL|field|beginMark_
name|String
name|beginMark_
decl_stmt|;
DECL|field|endMark_
name|String
name|endMark_
decl_stmt|;
DECL|field|beginPat_
name|Pattern
name|beginPat_
decl_stmt|;
DECL|field|endPat_
name|Pattern
name|endPat_
decl_stmt|;
DECL|field|slowMatch_
name|boolean
name|slowMatch_
decl_stmt|;
DECL|field|lookAhead_
name|int
name|lookAhead_
decl_stmt|;
comment|// bytes to read to try to synch CDATA/non-CDATA. Should be more than max record size
DECL|field|maxRecSize_
name|int
name|maxRecSize_
decl_stmt|;
DECL|field|bin_
name|BufferedInputStream
name|bin_
decl_stmt|;
comment|// Wrap FSDataInputStream for efficient backward seeks
DECL|field|pos_
name|long
name|pos_
decl_stmt|;
comment|// Keep track on position with respect encapsulated FSDataInputStream
DECL|field|NA
specifier|private
specifier|final
specifier|static
name|int
name|NA
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|firstMatchStart_
name|int
name|firstMatchStart_
init|=
literal|0
decl_stmt|;
comment|// candidate record boundary. Might just be CDATA.
DECL|field|firstMatchEnd_
name|int
name|firstMatchEnd_
init|=
literal|0
decl_stmt|;
DECL|field|synched_
name|boolean
name|synched_
decl_stmt|;
block|}
end_class

end_unit

