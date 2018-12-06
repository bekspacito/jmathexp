package edu.myrza.jmathexp.tokenizer;

import com.sun.istack.internal.Nullable;
import edu.myrza.jmathexp.common.Token;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

public class LexicalAnalizer{

        private Scanner scanner;
        private int pointer;
        private String exp;
        private List<String> functions;
        private Set<String> rsOperators;
        private Set<String> lsOperators;
        private Set<String> binaryOperators;

        private List<String> operators;
        private List<String> variables;
        private List<Token>  reserveSymbols;

        public LexicalAnalizer(String exp,
                               @Nullable Set<String> variables,
                               Set<String> functions,
                               Set<String> rsOperators,
                               Set<String> lsOperators,
                               Set<String> binaryOperators)
        {

            this.exp = exp.replaceAll("\\s+","") + "]";
            scanner = new Scanner(this.exp);
            pointer = 0;

            this.binaryOperators = binaryOperators;
            this.rsOperators = rsOperators;
            this.lsOperators = lsOperators;

            //we sorted an operators and functions by length scanner order to apply LONGEST MATCHING RULE
            this.functions = functions.stream()
                                        .sorted(comparingInt(String::length).reversed())
                                        .collect(toList());

            operators = Stream.of(rsOperators,lsOperators,binaryOperators)
                                .flatMap(Set::stream)
                                .distinct()
                                .sorted(comparingInt(String::length).reversed())
                                .collect(toList());

            if(variables != null)
            this.variables = variables.stream()
                                        .sorted(comparingInt(String::length).reversed())
                                        .collect(toList());

            reserveSymbols = asList(new Token(Token.Type.OPEN_PARENTHESES,"("),
                                    new Token(Token.Type.CLOSE_PARENTHESES,")"),
                                    new Token(Token.Type.FUNCTION_ARG_SEPARATOR,","),
                                    new Token(Token.Type.END,"]"));

        }

        public boolean hasNext(){ return pointer < exp.length(); }

        public List<Token> next(){

            if(pointer >= exp.length())
                throw new NoSuchElementException("no tokens left....");

            List<Token> result = new ArrayList<>();
            String nextTokenStr = null;

            //is operand
            if(Character.isDigit(exp.charAt(pointer))) {
                nextTokenStr = scanner.findInLine("([0-9]+(\\.[0-9]+)?|\\.[0-9]+)");
                pointer += nextTokenStr.length();
                result.add(new Token(Token.Type.OPERAND,nextTokenStr));
                return result;
            }

            if(Character.isLetter(exp.charAt(pointer))) {
                Optional<String> nextToken = findFunction();
                if(nextToken.isPresent()) {
                    pointer += nextToken.get().length();
                    result.add(new Token(Token.Type.FUNCTION, nextToken.get()));
                    return result;
                }

                nextToken = findVariable();
                if(nextToken.isPresent()){
                    pointer += nextToken.get().length();
                    result.add(new Token(Token.Type.VARIABLE, nextToken.get()));
                    return result;
                }
            }

            //is findOperator
            Optional<String> res = findOperator();
            if(res.isPresent()) {
                pointer += res.get().length();
                return getMatchedTokens(res.get());
            }


            //is reversed symbols
            Optional<Token> resToken = reserveSymbols.stream()
                    .filter(t -> scanner.findWithinHorizon("\\Q" + t.lexeme + "\\E",t.lexeme.length()) != null)
                    .findFirst();

            if(resToken.isPresent()){
                pointer += resToken.get().lexeme.length();
                result.add(resToken.get());
                return result;
            }

            throw new RuntimeException("An unknown token appeared ,starting at position : " + ++pointer);

        }

        private List<Token> getMatchedTokens(String nextTokenStr){

            List<Token> result = new ArrayList<>();

            if(lsOperators.stream().anyMatch(str -> str.equals(nextTokenStr)))
                result.add(new Token(Token.Type.LS_UNARY_OPERATOR,nextTokenStr));

            if(binaryOperators.stream().anyMatch(str -> str.equals(nextTokenStr)))
                result.add(new Token(Token.Type.BINARY_OPERATOR,nextTokenStr));

            if(rsOperators.stream().anyMatch(str -> str.equals(nextTokenStr)))
                result.add(new Token(Token.Type.RS_UNARY_OPERATOR,nextTokenStr));

            return result;
        }

        private Optional<String> findOperator(){
            return operators.stream()
                    .filter(str -> scanner.findWithinHorizon("\\Q" + str + "\\E",str.length()) != null)
                    .findFirst();
        }

        private Optional<String> findFunction(){
            return functions.stream()
                    .filter(str -> scanner.findWithinHorizon(str,str.length()) != null)
                    .findFirst();

        }

        private Optional<String> findVariable(){
            if(variables != null)
                return variables.stream()
                                .filter(str -> scanner.findWithinHorizon(str,str.length()) != null)
                                .findFirst();

            return Optional.empty();
        }

        public void finalize(){
            scanner.close();
        }

    }
